package emulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileConnectionImpl;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import ru.nnproject.kemulator.filemanagerapi.AbstractFileManager;
import ru.nnproject.kemulator.filemanagerapi.FileManagerException;

import static ru.nnproject.kemulator.filemanagerapi.FileManagerAPI.*;

public class FileManagerImpl implements AbstractFileManager {

	private String title;
	private int type;
	private JFileChooser fileChooser;
	private FileFilter filter;

	public FileManagerImpl(String title, int type) {
		this.title = title;
		this.type = type;
	}

	@Override
	public boolean openFile() {
		createFileChooser();
		fileChooser.setDialogTitle(title);
		if(filter != null) {
			fileChooser.addChoosableFileFilter(filter);
		}
		fileChooser.setAcceptAllFileFilterUsed(false);
		int i = fileChooser.showOpenDialog(new JPanel());
		return i == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public boolean openFile(String path) {
		createFileChooser(path);
		fileChooser.setDialogTitle(title);
		if(filter != null) {
			fileChooser.addChoosableFileFilter(filter);
		}
		int i = fileChooser.showOpenDialog(new JPanel());
		return i == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public boolean openFolder() {
		createFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle(title);
		int i = fileChooser.showOpenDialog(new JPanel());
		return i == JFileChooser.APPROVE_OPTION;
	}

	private void createFileChooser() {
		if (type == 0) {
			fileChooser = new JFileChooser();
		} else if (type == 1){
			fileChooser = new NativeJFileChooser();
		}
	}

	private void createFileChooser(String path) {
		if (type == 0) {
			fileChooser = new JFileChooser(path);
		} else if (type == 1){
			fileChooser = new NativeJFileChooser(path);
		}
	}

	@Override
	public boolean openFolder(String path) {
		createFileChooser(path);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogTitle(title);
		int i = fileChooser.showOpenDialog(new JPanel());
		return i == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public String getPath() {
		try {
			return fileChooser.getSelectedFile().getAbsolutePath();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public OutputStream getOutputStream() throws FileManagerException {
		try {
			return new FileOutputStream(getPath());
		} catch (FileNotFoundException e) {
			throw new FileManagerException("Cannot create output stream: " + e.toString());
		}
	}

	@Override
	public InputStream getInputStream() throws FileManagerException {
		try {
			return new FileInputStream(getPath());
		} catch (FileNotFoundException e) {
			throw new FileManagerException("Cannot create input stream: " + e.toString());
		}
	}

	@Override
	public FileConnection getFileConnection() throws FileManagerException {
		return new DirectFileConnectionImpl(getPath());
	}

	@Override
	public boolean saveFile() {
		createFileChooser();
		if(filter != null) {
			fileChooser.setFileFilter(filter);
		}
		fileChooser.setDialogTitle(title);
		int i = fileChooser.showSaveDialog(new JPanel());
		return i == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public boolean saveFile(String path) {
		createFileChooser(path);
		if(filter != null) {
			fileChooser.setFileFilter(filter);
		}
		fileChooser.setDialogTitle(title);
		int i = fileChooser.showSaveDialog(new JPanel());
		return i == JFileChooser.APPROVE_OPTION;
	}

	@Override
	public void setFilterExtensions(String[] extensions, String description) {
		filter = new FileNameExtensionFilter(description, extensions);
	}

	@Override
	public void setFilterExtension(String ext, String description) {
		filter = new FileNameExtensionFilter(description, ext);
	}

	@Override
	public String getFileData() throws FileManagerException {
		try {
			return new String(Files.readAllBytes(Paths.get(getPath())), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new FileManagerException("Cannot get file data: " + e.toString());
		}
	}

}
