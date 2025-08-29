package Ivancompany.nbanktest.api.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileRequest {
    private String name;
}