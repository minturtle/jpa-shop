package jpabook.jpashop.service;

import jpabook.jpashop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ProductRepository productRepository;

}
