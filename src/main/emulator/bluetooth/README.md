# Bluetooth LAN backend internals

This directory implements KEmulator's built-in JSR-82 backend. It is a LAN
transport for KEmulator instances, **not** a wrapper around the host operating
system's native Bluetooth adapter. Discovery is sent over UDP and all SDP,
SPP, L2CAP, and GOEP/OBEX traffic is carried by TCP.

This document is intentionally kept next to the implementation: it describes
the private KEmulator-to-KEmulator protocol and must be updated together with
both sides of a wire-format change.

## Entry points and ownership

```text
MIDlet JSR-82 API / Connector / Vodafone API
                 |
                 v
      BluetoothBackendProvider
                 |
                 v
    BluetoothBackend (interface)
                 |
                 v
 BluetoothStack (default LAN backend)
       |              |              |
       v              v              v
DiscoveryManager   SDPServer   ServiceRegistry
```

- `BluetoothBackend` is the contract used by the public API facades.
- `BluetoothBackendProvider` owns the process-wide backend lifecycle. The
  default selector is `lan`, which creates `BluetoothStack`; a future native
  backend can be selected with a fully qualified class name and must implement
  `BluetoothBackend` with a public no-argument constructor.
- `BluetoothStack` owns local identity, the discovery manager, SDP listener,
  service registry, connection opening, and shutdown.
- `CustomMethod.close()` calls `BluetoothBackendProvider.shutdown()`. Notifier
  close paths unregister through the backend interface, not through a concrete
  LAN singleton.

The built-in backend starts when a JSR-82 facade or `Connector` first needs it.
It starts the TCP SDP listener before UDP discovery; if discovery cannot bind,
the partial SDP listener is stopped again.

## Host configuration

`BluetoothConfiguration` resolves host-only settings in this order:

1. non-empty JVM `-D` property;
2. an explicit entry in `AppSettings.systemProperties`;
3. an explicit entry in `Settings.systemProperties`;
4. matching environment variable.

The two System Properties maps are already persisted and editable by the
emulator. Their existing `:other.property` indirection and `:null` convention
are honored. These keys are read while the backend is created, so change them
before the first Bluetooth API call.

| Setting | JVM / System Properties key | Environment key | Default |
| --- | --- | --- | --- |
| Discovery UDP port | `kemulator.bluetooth.discovery.port` | `KEM_BT_DISCOVERY_PORT` | `63520` |
| SDP TCP port | `kemulator.bluetooth.sdp.port` | `KEM_BT_SDP_PORT` | `0` (OS-assigned) |
| Manual peers | `kemulator.bluetooth.peers` | `KEM_BT_PEERS` | none |
| Backend selector | `kemulator.bluetooth.backend` | `KEM_BT_BACKEND` | `lan` |
| Local Bluetooth address | `bluetooth.address` | `KEM_BT_ADDRESS` | generated 12-hex address |
| Friendly name | `bluetooth.friendly.name` | `KEM_BT_NAME` | `KEmulator-xxxx` |

A non-default discovery port is deliberately noisy: automatic discovery only
works when every participating emulator uses the **same** discovery port.
Use manual peers when multicast or broadcast is blocked or when the remote
side intentionally uses a different discovery port.

Manual-peer syntax is a comma- or semicolon-separated list:

```text
BT_ADDRESS@host:sdpPort
001122AABBCC@192.168.1.42:63521;AABBCCDDEEFF@host.example:63522
```

Configured peers are entered in the normal peer routing table and are also
returned by `DiscoveryAgent.retrieveDevices(DiscoveryAgent.PREKNOWN)`. They
remain addressable if a `BYE` packet is seen. A local address or malformed
entry is ignored.

## Discovery protocol

`DiscoveryManager` owns one reusable `MulticastSocket` bound to the configured
UDP discovery port. It joins `239.255.10.10` where available and accepts normal
broadcast traffic on the same socket.

An inquiry additionally creates an **ephemeral reply socket**. Requests are
sent from that socket to each directed IPv4 broadcast address, the limited
broadcast address, the multicast group, and loopback. A responder unicasts its
answer to the request source port. This is important on one PC: several
KEmulator processes can share the discovery listener port, but each inquiry
still receives its own replies.

Datagrams are UTF-8 strings separated by `|`:

```text
KEM_BT|DISCOVER_REQ|btAddress|friendlyName|advertisedIp|sdpPort
KEM_BT|DISCOVER_RESP|btAddress|friendlyName|advertisedIp|sdpPort|deviceClass|discoverable
KEM_BT|BYE|btAddress
```

The advertised address is informational. The receiver caches the UDP packet's
actual source address as the route for SDP and service traffic; this avoids
choosing an unreachable interface on a multi-homed peer.

## SDP and service lookup

`SDPServer` listens on the configured TCP port. Port `0` binds once and keeps
the OS-reserved port; there is no probe-then-bind free-port helper.

The request is one nullable UTF-8 string encoded by `BluetoothWireCodec`:

```text
SEARCH <uuid1,uuid2,...>
SEARCH ALL
LIST
```

The response begins with an `int` service count. Each record is:

```text
string uuidOrPsm
string serviceName
string protocol                 # btspp, btl2cap, or btgoep
int    serviceTcpPort
int    serviceRecordHandle
string connectionUrl
int    deviceServiceClass
int    attributeIdCount
int[]  attributeIds
```

A `string` is `int UTF-8-byte-length + bytes`, with `-1` for `null`. Service
search normalizes dashed and undashed UUID spellings at the boundary. The
receiver rebuilds the essential `ServiceRecordImpl` fields and basic attributes.
Service count and attribute-ID count are bounded to 256 and 1024 respectively.

`ServiceRegistry` assigns the service-record handle during registration, maps
notifiers to records, and indexes service identifiers through
`BluetoothUtils.normalizeServiceIdentifier`. Closing a notifier unregisters and
closes its associated service.

## Opening service connections

A server URL (`btspp://localhost:...`, `btl2cap://localhost:...`, or
`btgoep://localhost:...`) creates a TCP `ServerSocket(0)` directly. The OS
therefore reserves the selected port atomically, and the service is registered
in SDP with that port.

For a client URL, `BluetoothStack` resolves the Bluetooth address in the peer
cache, asks the peer's SDP listener for the requested UUID/PSM, then opens a
bounded-timeout TCP connection to the returned service port. The transport
objects are:

| JSR-82/GOEP scheme | LAN implementation |
| --- | --- |
| `btspp` | `BTSPPConnection` TCP stream |
| `btl2cap` | `BTL2CAPConnection` length-aware TCP transport |
| `btgoep` | `obex.ClientSessionImpl` over TCP |

SPP is a raw TCP stream. L2CAP preserves packet boundaries as
`unsigned-short big-endian length + payload`; MTUs are clamped to 48..672 and a
receive buffer smaller than the packet gets the prefix while the remainder is
discarded, matching the existing JSR-82 emulation behavior.

## Shared binary primitives

`BluetoothWireCodec` defines the binary primitives used by SDP and OBEX:

- lengths, counts, and ordinary `int` fields are signed big-endian 32-bit values;
- a nullable string or blob is `int length + bytes`; `-1` means `null`;
- a frame is `int frameLength + exactly frameLength bytes`;
- strings are UTF-8, never Java modified-UTF;
- incoming strings are limited to 64 KiB, blobs/frames to 16 MiB, and remote
  counts are checked before collection allocation.

Do not replace these primitives with Java object serialization, class-name
strings, or a modified-UTF helper. Both endpoints need the same explicit format
for LAN interoperability.

## OBEX wire format

The private GOEP transport is implemented in `obex/ObexWireCodec`. Every
request and response is first written to a `ByteArrayOutputStream`, then sent
as one `BluetoothWireCodec` frame. This ensures that the length prefix is real,
not a placeholder.

Operations are one byte:

| Value | Operation |
| --- | --- |
| `0` | CONNECT |
| `1` | DISCONNECT |
| `2` | PUT |
| `3` | GET |
| `4` | SETPATH |
| `5` | DELETE |

Request payload:

```text
byte operation
int  headerCount
header[headerCount]
blob requestBody
```

Response payload:

```text
int  headerCount
header[headerCount]
int  responseCode
blob responseBody
```

Each header is `int id + byte type + typed payload`. The type tags are:

| Tag | Value type | Payload |
| --- | --- | --- |
| `0` | null | none |
| `1` | `String` | shared UTF-8 string |
| `2` | `Long` | `long` |
| `3` | `Integer` | `int` |
| `4` | `byte[]` | shared blob |
| `5` | `Boolean` | boolean byte |
| `6` | `Calendar` | epoch milliseconds (`long`) |

At most 256 headers are accepted. The decoder rejects unknown tags, invalid
lengths/counts, and trailing frame bytes. `ClientSessionImpl` and
`SessionNotifierImpl` must remain symmetric when this protocol changes.

## Maintenance rules

1. Keep `javax.bluetooth`, `Connector`, the L2CAP facades, Vodafone API, and
   shutdown code dependent on `BluetoothBackend`, not `BluetoothStack`.
2. Keep LAN-specific routing, UDP, TCP, and SDP implementation under this
   package so another backend can be added without changing the public APIs.
3. When changing a wire format, update the reader and writer together, preserve
   bounds, and test two separate emulator processes as well as a real LAN
   interface where available.
4. Do not reintroduce a `findFreePort()` probe: bind the actual listener to
   port `0` instead.
