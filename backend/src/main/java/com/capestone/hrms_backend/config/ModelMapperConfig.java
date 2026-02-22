package com.capestone.hrms_backend.config;

import com.capestone.hrms_backend.dto.response.*;
import com.capestone.hrms_backend.entity.community.*;
import com.capestone.hrms_backend.entity.expense.Expense;
import com.capestone.hrms_backend.entity.expense.ExpenseProof;
import com.capestone.hrms_backend.entity.job.JobCvReviewer;
import com.capestone.hrms_backend.entity.job.JobOpening;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.travel.TravelDocument;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import org.modelmapper.Conditions;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();


        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setPropertyCondition(Conditions.isNotNull());


        Converter<Employee, String> empToFullName = ctx -> {
            Employee e = ctx.getSource();
            if (e == null) return null;
            return Arrays.asList(e.getFirstName(), e.getMiddleName(), e.getLastName())
                    .stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .reduce((a, b) -> a + " " + b)
                    .orElse(null);
        };


        //HR id from: TravelPlan to TravelResponseDto
        modelMapper.typeMap(TravelPlan.class, TravelPlanResponseDto.class)
                .addMappings(m-> {
                    m.map(plan -> plan.getCreatedBy().getId(), TravelPlanResponseDto::setCreatedBy);
                    m.map(TravelPlan::getDepatureDate,TravelPlanResponseDto::setDepartureDate);
                });

        modelMapper.typeMap(Expense.class,ExpenseResponseDto.class)
                        .addMappings(m->{
                            m.map(exp -> exp.getEmployee().getId(),ExpenseResponseDto::setEmployeeId);
                            m.map(exp->exp.getCategory().getName(),ExpenseResponseDto::setCategory);
                            m.map(exp -> exp.getReviewedBy().getId(),ExpenseResponseDto::setReviewedBy);
                        });

//        modelMapper.typeMap(TravelDocument.class,TravelDocumentResponseDto.class)
//                .addMappings(m->{
//                    m.map(TravelDocument::getDocType,TravelDocumentResponseDto::setDocType);
//                });

        modelMapper.typeMap(TravelDocument.class,TravelDocumentResponseDto.class)
                .addMappings(m->{
                    m.map(doc -> doc.getUploadedBy().getId(),TravelDocumentResponseDto::setUploadedById);
                    m.map(doc -> doc.getUploadedFor().getId(),TravelDocumentResponseDto::setUploadedForId);
                });

        modelMapper.typeMap(ExpenseProof.class, ExpenseProofResponseDto.class)
                .addMappings(m->{
                    m.map(doc -> doc.getExpense().getEmployee().getId(),ExpenseProofResponseDto::setUploadedById);
                });
//        modelMapper.typeMap(Employee.class, EmployeeShorterResponseDto.class)
//                .addMappings(m->{
//                    m.map(emp->emp.getFirstName()+" "+emp.getLastName(),EmployeeShorterResponseDto::setName);
//                });

        modelMapper.typeMap(JobOpening.class, JobOpeningResponseDto.class)
                .addMappings(m->{
                    m.map(opening -> opening.getCreatedBy().getId(),JobOpeningResponseDto::setHrId);
                });

        modelMapper.typeMap(JobCvReviewer.class,JobOpeningReviewerResponseDto.class)
                .addMappings(m->{
                   m.map(reviewer->reviewer.getReveiwer().getId(),JobOpeningReviewerResponseDto::setId);
                   m.map(reviewer->reviewer.getReveiwer().getFirstName(),JobOpeningReviewerResponseDto::setName);
                   m.map(reviewer->reviewer.getReveiwer().getUser().getEmail(),JobOpeningReviewerResponseDto::setEmail);
                });
        modelMapper.typeMap(Employee.class, EmployeeProfileDto.class)
                .addMappings(m->{
                    m.map(emp->emp.getUser().getEmail(),EmployeeProfileDto::setEmail);
                    m.map(emp->emp.getDepartment().getName(),EmployeeProfileDto::setDepartment);
                    m.map(emp->emp.getRole().getName(),EmployeeProfileDto::setRole);
                    m.map(emp->emp.getManager().getFirstName(),EmployeeProfileDto::setManagerName);
                });


        // Employee -> ActorDto
        modelMapper.typeMap(Employee.class, ActorDto.class).addMappings(m -> {

            m.map(Employee::getId, ActorDto::setId);
            m.using(empToFullName).map(src -> src, ActorDto::setFullName);
            m.map(emp -> emp.getUser().getEmail(), ActorDto::setEmail);

        });


        modelMapper.typeMap(AchievementPost.class, PostResponseDto.class)
                .addMappings(m -> {
                    m.map(AchievementPost::getId, PostResponseDto::setId);
                    m.map(AchievementPost::getTitle, PostResponseDto::setTitle);
                    m.map(AchievementPost::getDescription, PostResponseDto::setDescription);
                    m.map(AchievementPost::getVisibility, PostResponseDto::setVisibility);
                    m.map(AchievementPost::isSystemGenerated, PostResponseDto::setSystemGenerated);
                    m.map(AchievementPost::getLikeCount, PostResponseDto::setLikeCount);
                    m.map(AchievementPost::getCommentCount, PostResponseDto::setCommentCount);
                    m.map(AchievementPost::getCreatedAt, PostResponseDto::setCreatedAt);
                    m.map(AchievementPost::getUpdatedAt, PostResponseDto::setUpdatedAt);
                    m.map(AchievementPost::getDeletedAt, PostResponseDto::setDeletedAt);

                    // This will use Employee -> ActorDto automatically
                    //m.map(AchievementPost::getAuthor, PostResponseDto::setAuthor);
                });


        // PostComment -> CommentResponseDto
        modelMapper.typeMap(PostComment.class, CommentResponseDto.class).addMappings(m -> {
            m.map(src -> src.getPost().getId(), CommentResponseDto::setPostId);
            //m.map(PostComment::getAuthor, CommentResponseDto::setAuthor);
        });

        // ModerationAction -> ModerationResponseDto
        modelMapper.typeMap(ModerationAction.class, ModerationResponseDto.class).addMappings(m -> {
//            m.map(ModerationAction::getActor, ModerationResponseDto::setActor);
        });

        // CelebrationJob -> CelebrationJobResponseDto
        modelMapper.typeMap(CelebrationJob.class, CelebrationJobResponseDto.class).addMappings(m -> {
//            m.map(CelebrationJob::getEmployee, CelebrationJobResponseDto::setEmployee);
            m.map(src -> src.getPost() != null ? src.getPost().getId() : null,
                    CelebrationJobResponseDto::setPostId);
        });


        // PostLike -> LikeResponseDto
        modelMapper.typeMap(PostLike.class, LikeResponseDto.class).addMappings(m -> {
            m.map(PostLike::getId, LikeResponseDto::setId);
            m.map(src -> src.getPost().getId(), LikeResponseDto::setPostId);
//            m.map(PostLike::getEmployee, LikeResponseDto::setEmployee); // uses Employee -> ActorDto
            m.map(PostLike::getCreatedAt, LikeResponseDto::setCreatedAt);
        });

// PostAttachment -> AttachmentResponseDto
        modelMapper.typeMap(PostAttachment.class, AttachmentResponseDto.class).addMappings(m -> {
            m.map(PostAttachment::getId, AttachmentResponseDto::setId);
            m.map(src -> src.getPost().getId(), AttachmentResponseDto::setPostId);
            m.map(PostAttachment::getFileUrl, AttachmentResponseDto::setFileUrl);
            m.map(PostAttachment::getFileName, AttachmentResponseDto::setFileName);
            m.map(PostAttachment::getMimeType, AttachmentResponseDto::setMimeType);
//            m.map(PostAttachment::getUploadedBy, AttachmentResponseDto::setUploadedBy); // Employee -> ActorDto applies
            m.map(PostAttachment::getCreatedAt, AttachmentResponseDto::setCreatedAt);
        });

        return modelMapper;
    }
}
