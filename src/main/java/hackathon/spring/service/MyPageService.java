package hackathon.spring.service;

import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.domain.Member;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MyPageService {
    private final MemberRepository memberRepository;
    private final NoteRepository noteRepository;

    public String getMemberNicknameByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(Member::getNickname)  // Member 객체에서 닉네임 추출
                .orElseThrow(() -> new RuntimeException(String.valueOf(ErrorStatus._MEMBER_NOT_FOUND)));
    }

    // 커피기록개수 get
    public Integer getCoffeeRecordNum(Long memberId) {
        return noteRepository.countByMemberId(memberId);
    }


    // 전체 추천 기록 get

}
