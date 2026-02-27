package com.capestone.hrms_backend.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileStorageService {
    final private String basePath = "uploads/";

    public String save(Long travelId, MultipartFile file)throws IOException {

        //Appending Travel Id
        String folder = basePath + travelId + "/";

        //Creating directory
        Files.createDirectories(Paths.get(folder));

        //Generating name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        //Generating path
        Path path = Paths.get(folder+fileName);

        //Saving File
        Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }

    public String saveExp(Long expenseId, MultipartFile file)throws IOException {

        //Appending Travel Id
        String folder = basePath + "expenses/" + expenseId + "/";

        //Creating directory
        Files.createDirectories(Paths.get(folder));

        //Generating name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        //Generating path
        Path path = Paths.get(folder+fileName);

        //Saving File
        Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }

    public String saveJd(String title, MultipartFile file)throws IOException{
        String folder = basePath + "jd/" + title + "/";

        //Creating directory
        Files.createDirectories(Paths.get(folder));

        //Generating name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        //Generating path
        Path path = Paths.get(folder+fileName);

        //Saving File
        Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }

    public String saveCv(Long refId,MultipartFile file) throws IOException{
        String folder = basePath + "cvs/" + refId + "/";

        //Creating directory
        Files.createDirectories(Paths.get(folder));

        //Generating name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        //Generating path
        Path path = Paths.get(folder+fileName);

        //Saving File
        Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }


    public String savePostAttachment(Long postId, MultipartFile file) throws IOException {
        String folder = basePath + "posts/" + postId + "/";

        //Creating directory
        Files.createDirectories(Paths.get(folder));

        //Generating name
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        //Generating path
        Path path = Paths.get(folder + fileName);

        //Saving File
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString(); // return public URL if you have a serving layer
    }

    public String saveSocialPostImage(Long postId, MultipartFile file) throws IOException {
        String folder = basePath + "social/" + postId + "/";

        Files.createDirectories(Paths.get(folder));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(folder + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return path.toString();
    }

    public String saveProfile(Long empId, MultipartFile file) throws IOException {
        String folder = basePath + "profiles/" + empId + "/";
        Files.createDirectories(Paths.get(folder));
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(folder + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return path.toString();
    }


    public byte[] read(String path) throws IOException{
        return Files.readAllBytes(Paths.get(path));
    }

    public void delete(String path) throws IOException{
        Files.deleteIfExists(Paths.get(path));
        return;
    }
}
