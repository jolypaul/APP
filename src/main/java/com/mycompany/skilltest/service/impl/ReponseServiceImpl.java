package com.mycompany.skilltest.service.impl;

import com.mycompany.skilltest.domain.Reponse;
import com.mycompany.skilltest.repository.ReponseRepository;
import com.mycompany.skilltest.service.ReponseService;
import com.mycompany.skilltest.service.dto.ReponseDTO;
import com.mycompany.skilltest.service.mapper.ReponseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.skilltest.domain.Reponse}.
 */
@Service
@Transactional
public class ReponseServiceImpl implements ReponseService {

    private static final Logger LOG = LoggerFactory.getLogger(ReponseServiceImpl.class);

    private final ReponseRepository reponseRepository;

    private final ReponseMapper reponseMapper;

    public ReponseServiceImpl(ReponseRepository reponseRepository, ReponseMapper reponseMapper) {
        this.reponseRepository = reponseRepository;
        this.reponseMapper = reponseMapper;
    }

    @Override
    public ReponseDTO save(ReponseDTO reponseDTO) {
        LOG.debug("Request to save Reponse : {}", reponseDTO);
        Reponse reponse = reponseMapper.toEntity(reponseDTO);
        reponse = reponseRepository.save(reponse);
        return reponseMapper.toDto(reponse);
    }

    @Override
    public ReponseDTO update(ReponseDTO reponseDTO) {
        LOG.debug("Request to update Reponse : {}", reponseDTO);
        Reponse reponse = reponseMapper.toEntity(reponseDTO);
        reponse = reponseRepository.save(reponse);
        return reponseMapper.toDto(reponse);
    }

    @Override
    public Optional<ReponseDTO> partialUpdate(ReponseDTO reponseDTO) {
        LOG.debug("Request to partially update Reponse : {}", reponseDTO);

        return reponseRepository
            .findById(reponseDTO.getId())
            .map(existingReponse -> {
                reponseMapper.partialUpdate(existingReponse, reponseDTO);

                return existingReponse;
            })
            .map(reponseRepository::save)
            .map(reponseMapper::toDto);
    }

    public Page<ReponseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reponseRepository.findAllWithEagerRelationships(pageable).map(reponseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReponseDTO> findOne(Long id) {
        LOG.debug("Request to get Reponse : {}", id);
        return reponseRepository.findOneWithEagerRelationships(id).map(reponseMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Reponse : {}", id);
        reponseRepository.deleteById(id);
    }
}
