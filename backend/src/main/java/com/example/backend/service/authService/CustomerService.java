package com.example.backend.service.authService;

import com.example.backend.dto.auth.CustomerDTO;
import com.example.backend.entity.auth.Customer;
import com.example.backend.repository.auth.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    public Customer create(CustomerDTO dto){

        Customer c = new Customer();

        c.setCode(dto.getCode());
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setPhoneNumber(dto.getPhoneNumber());

        c.setGender(dto.getGender() != null ? dto.getGender() : true);

        c.setBirthday(dto.getBirthday());

        c.setAccount(dto.getAccount());
        c.setPassword(dto.getPassword());

        c.setCreateAt(new Date());
        c.setUpdatedAt(new Date());

        c.setStatus(dto.getStatus() != null ? dto.getStatus() : true);

        return repo.save(c);
    }

    public Customer update(Integer id, CustomerDTO dto){

        Customer c = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setEmail(dto.getEmail());
        c.setPhoneNumber(dto.getPhoneNumber());
        c.setGender(dto.getGender());

        c.setUpdatedAt(new Date());

        return repo.save(c);
    }

    public void delete(Integer id){
        repo.deleteById(id);
    }

    public Customer getById(Integer id){
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    public List<Customer> getAll(){
        return repo.findAll();
    }

    public List<Customer> search(String keyword){
        return repo.findAll()
                .stream()
                .filter(c ->
                        c.getFirstName().toLowerCase().contains(keyword.toLowerCase()) ||
                                c.getLastName().toLowerCase().contains(keyword.toLowerCase()) ||
                                (c.getEmail()!=null && c.getEmail().toLowerCase().contains(keyword.toLowerCase())) ||
                                (c.getPhoneNumber()!=null && c.getPhoneNumber().contains(keyword)) ||
                                c.getCode().toLowerCase().contains(keyword.toLowerCase())
                )
                .toList();
    }
}