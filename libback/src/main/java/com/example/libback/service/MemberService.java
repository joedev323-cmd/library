package com.example.libback.service;

import com.example.libback.model.Member;
import com.example.libback.repository.MemberRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository borrowerRepository;

    public MemberService(MemberRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public List<Member> getAllMembers() {
        return borrowerRepository.findAll();
    }

    public Member saveMember(Member borrower) {
        return borrowerRepository.save(borrower);
    }
}