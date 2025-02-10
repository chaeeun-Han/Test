package com.example.myapp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class S3FileUploader {

	// 버킷 이름 동적 할당
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	// 버킷 주소 동적 할당
	@Value("${cloud.aws.s3.bucket.url}")
	private String defaultUrl;

	@Autowired
	private AmazonS3 s3Client;

	// 리스트 S3 업로드 - 고범준
	@Transactional
	public List<String> uploadFiles(List<MultipartFile> files, String table, String purpose, Long id) {
		List<String> Urls = new ArrayList<>();
		try {

			// 업로드된 각 파일 처리
			for (MultipartFile file : files) {
				File fileObj = convertMultiPartFileToFile(file);
				String fileName = table + "_" + purpose + "_" + id;

				if (purpose.equals("classFile") || purpose.equals("questionImage")) {
					fileName += "_" + UUID.randomUUID();
				}

				// key가 존재하면 기존 파일은 삭제
				if (s3Client.doesObjectExist(bucketName, fileName)) {
					s3Client.deleteObject(bucketName, fileName);
					log.info("deleted: {}", fileName);
				}

				// 파일 업로드
				s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
				log.info("S3 uploaded: {}", fileName);
				fileObj.delete();

				// S3에 저장된 이미지 호출하기
				URL url = s3Client.getUrl(bucketName, fileName);
				String imageUrl = "" + url;

				log.info(imageUrl);
				Urls.add(imageUrl);

			}

		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return Urls;
	}

	// 단일 S3 업로드 - 고범준
	public String uploadFile(MultipartFile file, String table, String purpose, Long id) {
		String UrlString = new String();
		try {

			// 업로드된 각 파일 처리
			File fileObj = convertMultiPartFileToFile(file);
			String fileName = table + "_" + purpose + "_" + id;

			if (purpose.equals("classFile")) {
				fileName += "_" + UUID.randomUUID();
			}

			// key가 존재하면 기존 파일은 삭제
			if (s3Client.doesObjectExist(bucketName, fileName)) {
				s3Client.deleteObject(bucketName, fileName);
				log.info("deleted: {}", fileName);
			}

			// 파일 업로드
			s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
			log.info("S3 uploaded: {}", fileName);
			fileObj.delete();

			// S3에 저장된 이미지 호출하기
			URL url = s3Client.getUrl(bucketName, fileName);
			UrlString = "" + url;

		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return UrlString;
	}

	private static File convertMultiPartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			log.error("Error converting multipartFile to file", e);
		}
		return convertedFile;
	}

}