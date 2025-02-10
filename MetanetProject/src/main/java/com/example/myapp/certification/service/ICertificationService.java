package com.example.myapp.certification.service;

public interface ICertificationService {

	boolean checkCourable(Long memberUID, String lecture_id);

	byte[] getCertification(Long memberUID, String lecture_id);

}
