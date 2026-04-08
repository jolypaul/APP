package com.mycompany.skilltest.service.impl;

import com.mycompany.skilltest.domain.Test;
import com.mycompany.skilltest.repository.TestRepository;
import com.mycompany.skilltest.service.TestService;
import com.mycompany.skilltest.service.dto.TestDTO;
import com.mycompany.skilltest.service.mapper.TestMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.skilltest.domain.Test}.
 */
@Service
@Transactional
public class TestServiceImpl implements TestService {

    private static final Logger LOG = LoggerFactory.getLogger(TestServiceImpl.class);

    private final TestRepository testRepository;

    private final TestMapper testMapper;

    public TestServiceImpl(TestRepository testRepository, TestMapper testMapper) {
        this.testRepository = testRepository;
        this.testMapper = testMapper;
    }

    @Override
    public TestDTO save(TestDTO testDTO) {
        LOG.debug("Request to save Test : {}", testDTO);
        Test test = testMapper.toEntity(testDTO);
        test = testRepository.save(test);
        return testMapper.toDto(test);
    }

    @Override
    public TestDTO update(TestDTO testDTO) {
        LOG.debug("Request to update Test : {}", testDTO);
        Test test = testMapper.toEntity(testDTO);
        test = testRepository.save(test);
        return testMapper.toDto(test);
    }

    @Override
    public Optional<TestDTO> partialUpdate(TestDTO testDTO) {
        LOG.debug("Request to partially update Test : {}", testDTO);

        return testRepository
            .findById(testDTO.getId())
            .map(existingTest -> {
                testMapper.partialUpdate(existingTest, testDTO);

                return existingTest;
            })
            .map(testRepository::save)
            .map(testMapper::toDto);
    }

    public Page<TestDTO> findAllWithEagerRelationships(Pageable pageable) {
        return testRepository.findAllWithEagerRelationships(pageable).map(testMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestDTO> findOne(Long id) {
        LOG.debug("Request to get Test : {}", id);
        return testRepository.findOneWithEagerRelationships(id).map(testMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Test : {}", id);
        testRepository.deleteById(id);
    }
}
