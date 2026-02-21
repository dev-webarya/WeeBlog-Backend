package com.blogapp.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Archive index — year/month breakdown with blog counts")
public class ArchiveResponse {

    @Schema(description = "Year")
    private int year;

    @Schema(description = "Monthly breakdown for this year")
    private List<MonthCount> months;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthCount {

        @Schema(description = "Month number (1–12)")
        private int month;

        @Schema(description = "Number of published blogs in this month")
        private long count;
    }
}
