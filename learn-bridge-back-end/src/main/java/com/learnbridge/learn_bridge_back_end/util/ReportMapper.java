package com.learnbridge.learn_bridge_back_end.util;

import com.learnbridge.learn_bridge_back_end.dto.ReportDTO;
import com.learnbridge.learn_bridge_back_end.entity.Report;

import java.util.List;
import java.util.stream.Collectors;

public class ReportMapper {

    public static ReportDTO toReportDTO(Report report) {
        return new ReportDTO(report);
    }

    public static List<ReportDTO> toReportDTOList(List<Report> reportList) {
        return reportList.stream().map(ReportMapper::toReportDTO).collect(Collectors.toList());
    }
}
