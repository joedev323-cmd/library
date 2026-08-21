package com.example.libback.service;

import com.example.libback.model.Borrower;
import com.example.libback.repository.BorrowerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MemberService {

    private final BorrowerRepository borrowerRepository;

    public MemberService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public List<Borrower> getAllMembers() {
        return borrowerRepository.findAll();
    }

    public Borrower saveMember(Borrower borrower) {
        return borrowerRepository.save(borrower);
    }
}