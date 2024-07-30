package com.amorgakco.backend.member.controller;

import com.amorgakco.backend.global.argumentresolver.AuthMember;
import com.amorgakco.backend.member.dto.AdditionalInfoRequest;
import com.amorgakco.backend.member.service.MemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAdditionalInfo(
            @RequestBody final AdditionalInfoRequest additionalInfoRequest,
            @AuthMember final Long memberId) {
        memberService.updateAdditionalInfo(additionalInfoRequest, memberId);
    }
}