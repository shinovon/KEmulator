package emulator.graphics3D.egl;

public final class GLConfiguration {
	public static boolean OES_draw_texture;
	public static boolean OES_matrix_pallete;
	public static boolean OES_texture_cube_map;
	public static boolean OES_blend_subtract;
	public static boolean OES_blend_func_separate;
	public static boolean OES_blend_equations_separate;
	public static boolean OES_framebuffer_object;

	public GLConfiguration() {
		super();
	}

	public static int method768(final int n) {
		switch (n) {
			case 2833:
			case 2849:
			case 2884:
			case 2886:
			case 2898:
			case 2900:
			case 2914:
			case 2915:
			case 2916:
			case 2917:
			case 2930:
			case 2931:
			case 2932:
			case 2961:
			case 2962:
			case 2963:
			case 2964:
			case 2965:
			case 2966:
			case 2967:
			case 2968:
			case 2976:
			case 2979:
			case 2980:
			case 2981:
			case 3009:
			case 3010:
			case 3040:
			case 3041:
			case 3056:
			case 3152:
			case 3153:
			case 3154:
			case 3156:
			case 3317:
			case 3333:
			case 3377:
			case 3378:
			case 3379:
			case 3382:
			case 3384:
			case 3385:
			case 3408:
			case 3410:
			case 3411:
			case 3412:
			case 3413:
			case 3414:
			case 3415:
			case 10752:
			case 32777:
			case 32824:
			case 32873:
			case 32890:
			case 32891:
			case 32892:
			case 32894:
			case 32895:
			case 32897:
			case 32898:
			case 32899:
			case 32904:
			case 32905:
			case 32906:
			case 32968:
			case 32969:
			case 32970:
			case 32971:
			case 33000:
			case 33001:
			case 34018:
			case 34076:
			case 34466:
			case 34468:
			case 34473:
			case 34474:
			case 34475:
			case 34877:
			case 34882:
			case 34886:
			case 34888:
			case 34966:
			case 34967:
			case 34968:
			case 34970:
			case 34974:
			case 35210:
			case 35211:
			case 35738:
			case 35739:
			case 35742:
			case 35743: {
				return 1;
			}
			case 2834:
			case 2850:
			case 2928:
			case 3386:
			case 33901:
			case 33902: {
				return 2;
			}
			case 2899:
			case 2918:
			case 2978:
			case 3088:
			case 3106:
			case 3107: {
				return 4;
			}
			case 34467: {
				return 10;
			}
			case 2982:
			case 2983:
			case 2984:
			case 35213:
			case 35214:
			case 35215: {
				return 16;
			}
			default: {
				System.out.println("GLConfiguration: glGetNumParams called with pname=" + n);
				return 0;
			}
		}
	}

	public static int method769() {
		return 1;
	}

	public static int method770(final int n) {
		switch (n) {
			case 4613:
			case 4614:
			case 4615:
			case 4616:
			case 4617: {
				return 1;
			}
			case 4612: {
				return 3;
			}
			case 4608:
			case 4609:
			case 4610:
			case 4611:
			case 5632: {
				return 4;
			}
			default: {
				System.out.println("GLConfiguration: glLightNumParams called with pname=" + n);
				return 0;
			}
		}
	}

	public static int method772(final int n) {
		switch (n) {
			case 2899: {
				return 4;
			}
			case 2898: {
				return 1;
			}
			default: {
				System.out.println("GLConfiguration: glLightModelNumParams called with pname=" + n);
				return 0;
			}
		}
	}

	public static int method773(final int n) {
		switch (n) {
			case 4608:
			case 4609:
			case 4610:
			case 5632: {
				return 4;
			}
			case 5633: {
				return 1;
			}
			default: {
				System.out.println("GLConfiguration: glMaterialNumParams called with pname=" + n);
				return 0;
			}
		}
	}

	public static int method774(final int n) {
		if (n == 33065) {
			return 3;
		}
		return 1;
	}

	public static int method775(final int n) {
		switch (n) {
			case 8704:
			case 34161:
			case 34162:
			case 34914: {
				return 1;
			}
			case 8705: {
				return 4;
			}
			default: {
				System.out.println("GLConfiguration: glTexEnvNumParams called with pname=" + n);
				return 0;
			}
		}
	}

	public static int method771() {
		return 1;
	}

	public static int method776(final int n) {
		switch (n) {
			case 10240:
			case 10241:
			case 10242:
			case 10243:
			case 33169: {
				return 1;
			}
			case 35741: {
				return 4;
			}
			default: {
				return 0;
			}
		}
	}

	static {
		GLConfiguration.OES_draw_texture = false;
		GLConfiguration.OES_matrix_pallete = true;
		GLConfiguration.OES_texture_cube_map = true;
		GLConfiguration.OES_blend_subtract = true;
		GLConfiguration.OES_blend_func_separate = true;
		GLConfiguration.OES_blend_equations_separate = true;
	}
}
