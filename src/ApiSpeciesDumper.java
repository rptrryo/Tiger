import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * <pre>
 * ApiSpeciesDumper
 *
 * 【how to use】
 * 　1. pass the CLASSPATH of target project
 * 　2. execute with argument of target project path
 *
 * 【restraction】
 * 　・can't dump the class occurs error on static initializer
 * </pre>
 */
public class ApiSpeciesDumper {

	private String _rootPath = null;

	private ApiSpeciesDumper(String sourceFolder) {
		_rootPath = new File(sourceFolder).getAbsolutePath();
	}

	/**
	 *
	 * @param sourceFolderPaths
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] sourceFolderPaths) throws IOException, ClassNotFoundException {

		if (sourceFolderPaths.length == 0) {
			System.out.println("designate source folder path at start argument. (you can designate multi arguments)");
			return;
		}

		for (String sourceFolderPath : sourceFolderPaths) {
			new ApiSpeciesDumper(sourceFolderPath).processFolder(new File(sourceFolderPath));
		}
	}

	private void processFolder(File folder) throws IOException, ClassNotFoundException {

		if (!folder.isDirectory()) {
			return;
		}

		for (File content : folder.listFiles()) {
			processFolderContent(content);
		}
	}

	private void processFolderContent(File content) throws ClassNotFoundException, IOException {

		if (isJavaFile(content)) {

			Class<?> clazz = toClass(content);
			if (clazz == null) {
				return;
			}

			dumpSignatures(clazz);

			for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
				dumpSignatures(declaredClass);
			}

		} else if (content.isDirectory()) {
			processFolder(content);
		} else {
			// ignore non java files.
		}
	}

	private void dumpSignatures(Class<?> clazz) {

		dumpConstructors(clazz);

		dumpMethods(clazz);
	}

	private void dumpConstructors(Class<?> clazz) {

		Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : declaredConstructors) {
			try {
				String packageName = clazz.getPackage().getName();
				String classVisibility = getVisibility(clazz.getModifiers());
				String type = getClassType(clazz);
				String className = createClassName(clazz);
				String methodVisibility = getVisibility(constructor);
				String returnType = "";
				String constructorSignature = createSignature(constructor);
				String annotationNames = formatAnnotations(constructor.getAnnotations());

				System.out.println(
						packageName + "\t"
						+ classVisibility + "\t"
						+ (Modifier.isAbstract(clazz.getModifiers()) ? "abstract" : "") + "\t"
						+ type + "\t"
						+ className + "\t"
						+ methodVisibility + "\t"
						+ returnType + "\t"
						+ constructorSignature + "\t"
						+ annotationNames);

			} catch (Error e) {
				System.out.println(e);
			}
		}
	}

	private void dumpMethods(Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {

			if (method.isBridge()) {
				continue;
			}

			try {
				String packageName = clazz.getPackage().getName();
				String classVisibility = getVisibility(clazz.getModifiers());
				String type = getClassType(clazz);
				String className = createClassName(clazz);
				String methodVisibility = getVisibility(method.getModifiers());
				String returnType = getReturnType(method);

				String methodSignature = createSignature(method);

				String annotationNames = formatAnnotations(method.getAnnotations());

				System.out.println(
						packageName + "\t"
						+ classVisibility + "\t"
						+ (Modifier.isAbstract(clazz.getModifiers()) ? "abstract" : "") + "\t"
						+ type + "\t"
						+ className + "\t"
						+ methodVisibility + "\t"
						+ returnType + "\t"
						+ methodSignature + "\t"
						+ annotationNames);

			} catch (Error e) {
				System.out.println(e);
			}
		}
	}

	private String createSignature(Constructor<?> constructor) {
		Type[] genericTypes = constructor.getGenericParameterTypes();
		Class<?>[] types = constructor.getParameterTypes();
		return constructor.getDeclaringClass().getSimpleName() + createParameterPart(genericTypes, types);
	}

	private String createSignature(Method method) {
		Type[] genericTypes = method.getGenericParameterTypes();
		Class<?>[] types = method.getParameterTypes();
		return method.getName() + createParameterPart(genericTypes, types);
	}

	private String createParameterPart(Type[] genericTypes, Class<?>[] types) {

		// in the case of inner class, the first argument of the constructor become out class. So correct index.
		String parameters = "";
		for (int i = types.length - genericTypes.length; i < genericTypes.length; i++) {
			String parameter = types[i].getSimpleName();
			if (genericTypes[i] instanceof TypeVariable) {
				parameter = genericTypes[i].toString();
			}
			parameters += (parameters.isEmpty() ? "" : ", ") + parameter;
		}

		return "(" + parameters  + ")";
	}

	private String getReturnType(Method method) {

		Type genericReturnType = method.getGenericReturnType();
		if (genericReturnType instanceof TypeVariable<?>) {
			return genericReturnType.toString();
		}

		String returnType = method.getReturnType().getSimpleName();
		return returnType;
	}

	private String getClassType(Class<?> clazz) {

		if (clazz.isInterface()) {
			return "interface";
		} else if (clazz.isEnum()) {
			return "enum";
		}
		return "class";
	}

	private String getVisibility(Constructor<?> constructor) {
		return getVisibility(constructor.getModifiers());
	}

	private String getVisibility(int modifiers) {

		if (Modifier.isPrivate(modifiers)) {
			return "private";
		} else if (Modifier.isProtected(modifiers)) {
			return "protected";
		} else if (Modifier.isPublic(modifiers)) {
			return "public";
		} else {
			return "";
		}
	}

	private String createClassName(Class<?> clazz) {

		String className = clazz.getSimpleName();

		Class<?> processingClass = clazz.getDeclaringClass();
		while (processingClass != null) {
			className = processingClass.getSimpleName() + "." + className;
			processingClass = processingClass.getDeclaringClass();
		}

		return className;
	}

	private String formatAnnotations(Annotation[] annotations) {
		String formattedAnnotations = "";
		for (Annotation annotation : annotations) {
			formattedAnnotations += (formattedAnnotations.isEmpty() ? "" : ", ") + "@" + annotation.annotationType().getSimpleName();
		}

		return formattedAnnotations;
	}

	private static boolean isJavaFile(File content) {

		return content.isFile() && content.getAbsolutePath().endsWith(".java");
	}

	private Class<?> toClass(File content) throws ClassNotFoundException {

		String className = getClassNameFromFilePath(content);

		try {
			return Class.forName(className, false, this.getClass().getClassLoader());
		} catch (Error error) {

			System.out.println("#### Failed to load " + className + " class. ###");
			return null;
		}
	}

	private String getClassNameFromFilePath(File javaFile) {

		String fullPath = javaFile.getAbsolutePath();
		String rootPathRemoved = fullPath.replace(this._rootPath + "\\", "");
		String extensionRemovedPath = rootPathRemoved.replace(".java", "");
		String className = extensionRemovedPath.replaceAll("\\\\", ".");

		return className;
	}
}
