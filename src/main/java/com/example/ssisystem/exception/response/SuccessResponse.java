package com.example.ssisystem.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(
        name = "Response",
        description = "Schema to hold successful response information"
)
@Data
public class SuccessResponse {

    @Schema(
            description = "Status code in the response"
    )
    private String statusCode;

    @Schema(
            description = "Status message in the response"
    )
    private String statusMsg;
    @Schema(
            description = "Status body in the response"
    )
    private Map<String , String> statusBody;

    public SuccessResponse(String statusCode, String statusMsg) {
        this.statusCode = statusCode;
        this.statusMsg = statusMsg;
    }

    public SuccessResponse(String statusCode, Map<String, String> statusBody) {
        this.statusCode = statusCode;
        this.statusBody = statusBody;
    }
}
