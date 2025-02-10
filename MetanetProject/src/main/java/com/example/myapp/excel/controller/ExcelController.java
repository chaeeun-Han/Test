package com.example.myapp.excel.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.myapp.common.response.ResponseCode;
import com.example.myapp.common.response.ResponseDto;
import com.example.myapp.common.response.ResponseMessage;
import com.example.myapp.excel.model.MemberForExcel;
import com.example.myapp.lecture.model.LectureList;
import com.example.myapp.lecture.service.ILectureService;
import com.example.myapp.member.service.IMemberService;
import com.example.myapp.util.GetAuthenUser;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    ILectureService lectureService;

    @Autowired
    IMemberService memberService;

    // excel 업로드 -- 고범준
    @SuppressWarnings({ "rawtypes", "resource" })
    @PostMapping(value = "/input-excel/lecture-list/{lecture_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> setRefundStatus(@PathVariable("lecture_id") Long lectureId,
            @RequestParam(value = "excelFile", required = true) MultipartFile excelFile) {
        String member_id = GetAuthenUser.getAuthenUser();
        // 인증되지 않은 경우는 바로 처리
        if (member_id == null) {
            return ResponseDto.noAuthentication();
        }

        Long memberId = lectureService.getMemberIdById(member_id);

        try {
            XSSFWorkbook excel = new XSSFWorkbook(excelFile.getInputStream());
            XSSFSheet workSheet = excel.getSheetAt(0);

            for (int i = 2; i < workSheet.getPhysicalNumberOfRows(); i++) {
                LectureList list = new LectureList();

                DataFormatter formatter = new DataFormatter();
                XSSFRow row = workSheet.getRow(i);

                String title = formatter.formatCellValue(row.getCell(0));
                String description = formatter.formatCellValue(row.getCell(1));
                String date = formatter.formatCellValue(row.getCell(2));
                String start_time = formatter.formatCellValue(row.getCell(3));
                String end_time = formatter.formatCellValue(row.getCell(4));

                list.setLecture_id(lectureId);
                list.setMember_id(memberId);

                list.setTitle(title + " - " + (i - 1) + " 일차");
                list.setDescription(description);
                list.setStart_time(date + "T" + start_time + ":00");
                list.setEnd_time(date + "T" + end_time + ":00");

                lectureService.insertLectureListByExcel(list);
            }
        } catch (Exception e) {
            return ResponseDto.databaseError();
        }
        ResponseDto responseBody = new ResponseDto(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        return ResponseEntity.ok(responseBody);
    }

    // excel 다운로드드 -- 고범준
    @SuppressWarnings({ "resource" })
    @GetMapping(value = "/get-excel/student/{lecture_id}")
    public ResponseEntity<byte[]> downloadStudentList(@PathVariable("lecture_id") Long lectureId) {
        String memberId = GetAuthenUser.getAuthenUser();
        if (memberId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<MemberForExcel> memberList = memberService.getMembersByLecture(lectureId);
            if (memberList.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            

            String title = memberList.get(0).getTitle().replaceAll("[\\\\/:*?\"<>|]", "");
            String fileName = title + "_학생목록.xlsx";

            ClassPathResource resource = new ClassPathResource("template/student-template.xlsx");
            FileInputStream fileInputStream = new FileInputStream(resource.getFile());
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFSheet titleSheet = workbook.getSheetAt(0);
            Row titleRow = titleSheet.getRow(0);
            titleRow.createCell(0).setCellValue("강의명: " + title);
            int rowIndex = 2;
            for (MemberForExcel member : memberList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(member.getName());
                row.createCell(1).setCellValue(member.getPhone());
                row.createCell(2).setCellValue(member.getEmail());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelData = outputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
