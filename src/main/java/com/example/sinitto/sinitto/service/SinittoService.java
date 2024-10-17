package com.example.sinitto.sinitto.service;

import com.example.sinitto.member.entity.Member;
import com.example.sinitto.sinitto.entity.SinittoBankInfo;
import com.example.sinitto.member.exception.MemberNotFoundException;
import com.example.sinitto.member.repository.MemberRepository;
import com.example.sinitto.sinitto.dto.SinittoBankRequest;
import com.example.sinitto.sinitto.dto.SinittoRequest;
import com.example.sinitto.sinitto.dto.SinittoResponse;
import com.example.sinitto.sinitto.exception.SinittoNotFoundException;
import com.example.sinitto.sinitto.repository.SinittoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SinittoService {

    private final MemberRepository memberRepository;
    private final SinittoRepository sinittoRepository;

    public SinittoService(MemberRepository memberRepository, SinittoRepository sinittoRepository) {
        this.memberRepository = memberRepository;
        this.sinittoRepository = sinittoRepository;
    }


    @Transactional
    public void createSinittoBankInfo(Long memberId, SinittoBankRequest sinittoBankRequest) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("이메일에 해당하는 멤버를 찾을 수 없습니다.")
        );
        SinittoBankInfo sinittoBankInfo = new SinittoBankInfo(sinittoBankRequest.bankName(), sinittoBankRequest.accountNumber(), member);
        sinittoRepository.save(sinittoBankInfo);
    }

    @Transactional(readOnly = true)
    public SinittoResponse readSinitto(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("이메일에 해당하는 멤버를 찾을 수 없습니다.")
        );
        SinittoBankInfo sinittoBankInfo = sinittoRepository.findByMemberId(memberId).orElseThrow(
                () -> new SinittoNotFoundException("이메일에 해당하는 멤버의 계좌정보를 찾을 수 없습니다.")
        );
        return new SinittoResponse(member.getName(), member.getPhoneNumber(), member.getEmail(), sinittoBankInfo.getAccountNumber(), sinittoBankInfo.getBankName());
    }

    @Transactional
    public void updateSinitto(Long memberId, SinittoRequest sinittoRequest) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("이메일에 해당하는 멤버를 찾을 수 없습니다.")
        );
        member.updateMember(sinittoRequest.name(), sinittoRequest.email(), sinittoRequest.phoneNumber());
    }

    @Transactional
    public void updateSinittoBankInfo(Long memberId, SinittoBankRequest sinittoBankRequest) {
        SinittoBankInfo sinittoBankInfo = sinittoRepository.findByMemberId(memberId).orElseThrow(
                () -> new SinittoNotFoundException("이메일에 해당하는 멤버의 계좌정보를 찾을 수 없습니다.")
        );
        sinittoBankInfo.updateSinitto(sinittoBankRequest.bankName(), sinittoBankRequest.accountNumber());
    }

    @Transactional
    public void deleteSinitto(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("이메일에 해당하는 멤버를 찾을 수 없습니다.")
        );
        memberRepository.delete(member);
    }

    @Transactional
    public void deleteSinittoBankInfo(Long memberId) {
        SinittoBankInfo sinittoBankInfo = sinittoRepository.findByMemberId(memberId).orElseThrow(
                () -> new SinittoNotFoundException("이메일에 해당하는 멤버의 계좌정보를 찾을 수 없습니다.")
        );
        sinittoRepository.delete(sinittoBankInfo);
    }

    @Transactional
    public List<SinittoResponse> readAllSinitto() {
        List<SinittoBankInfo> sinittoBankInfos = sinittoRepository.findAll();

        return sinittoBankInfos.stream()
                .map(m -> new SinittoResponse(m.getMember().getName(), m.getMember().getPhoneNumber(), m.getMember().getEmail(), m.getAccountNumber(), m.getBankName()))
                .toList();
    }

}
