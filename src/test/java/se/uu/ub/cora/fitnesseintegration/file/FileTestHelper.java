package se.uu.ub.cora.fitnesseintegration.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class FileTestHelper {
	public static FileTestHelper forDirectory(String basePath) throws IOException {
		return new FileTestHelper(basePath);
	}

	public String basePath;

	private FileTestHelper(String basePath) throws IOException {
		this.basePath = basePath;
		makeSureBasePathExistsAndIsEmpty();
	}

	private void makeSureBasePathExistsAndIsEmpty() throws IOException {
		File dir = new File(basePath);
		dir.mkdir();
		deleteFiles(basePath);
	}

	private void deleteFiles(String path) throws IOException {
		Stream<Path> list;
		list = Files.list(Paths.get(path));

		list.forEach(p -> deleteFile(p));
		list.close();
	}

	private void deleteFile(Path path) {
		try {
			if (path.toFile().isDirectory()) {
				deleteFiles(path.toString());
			}
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// <<<<<<< HEAD
	public void writeFileToDiskWithContentInFolderWithName(String content, String folderName,
			String fileName) throws IOException {
		String currentPath = possiblyCreateFolder(folderName);
		Path path = FileSystems.getDefault().getPath(currentPath, fileName);
		BufferedWriter writer;
		writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		writer.write(content, 0, content.length());
		writer.flush();
		writer.close();
	}

	private String possiblyCreateFolder(String folderName) {
		String[] folders = folderName.split("/");
		String currentPath = basePath;
		for (String folder : folders) {
			Path pathIncludingFolderName = Paths.get(currentPath, folder);
			File newPath = pathIncludingFolderName.toFile();
			if (!newPath.exists()) {
				newPath.mkdir();
			}
			currentPath += "/" + folder;
		}
		return currentPath;
		// =======
		// public void writeFileToDisk(String content, String folderName, String fileName)
		// throws IOException {
		// possiblyCreateFolder(folderName);
		// Path path = FileSystems.getDefault().getPath(basePath, folderName, fileName);
		// BufferedWriter writer;
		// writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
		// StandardOpenOption.CREATE);
		// writer.write(content, 0, content.length());
		// writer.flush();
		// writer.close();
		// }
		//
		// private void possiblyCreateFolder(String folderName) {
		// Path pathIncludingFolderName = Paths.get(basePath, folderName);
		// File newPath = pathIncludingFolderName.toFile();
		// if (!newPath.exists()) {
		// newPath.mkdir();
		// }
		// >>>>>>> branch 'issues/ALVIN-2668' of
		// https://github.com/lsu-ub-uu/cora-fitnesseintegration.git
	}

	public void removeFiles() throws IOException {
		if (Files.exists(Paths.get(basePath))) {
			deleteFiles(basePath);
			File dir = new File(basePath);
			dir.delete();
		}
	}
}