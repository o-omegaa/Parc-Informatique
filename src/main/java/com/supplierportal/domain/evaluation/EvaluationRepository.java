package com.supplierportal.domain.evaluation;
import java.util.*;
public interface EvaluationRepository {
    Evaluation save(Evaluation evaluation);
    Optional<Evaluation> findById(Long id);
    List<Evaluation> findBySupplierId(Long supplierId);
    List<Evaluation> findAll();
    void deleteById(Long id);
}
