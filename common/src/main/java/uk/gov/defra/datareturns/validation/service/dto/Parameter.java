package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Parameter extends BaseEntity {
    private String cas;
}
