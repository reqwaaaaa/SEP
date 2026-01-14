package com.pidu.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pidu.application.dto.ApplicationQueryDTO;
import com.pidu.application.dto.ApplicationSaveDTO;
import com.pidu.application.dto.AuditDTO;
import com.pidu.application.entity.SubsidyApplication;
import com.pidu.application.vo.ApplicationDetailVO;
import com.pidu.application.vo.ApplicationVO;
import com.pidu.common.entity.PageResult;

/**
 * 补贴申报服务接口
 */
public interface SubsidyApplicationService extends IService<SubsidyApplication> {

    /**
     * 提交申报
     */
    Long submitApplication(ApplicationSaveDTO saveDTO);

    /**
     * 更新申报（退回修改后）
     */
    void updateApplication(Long id, ApplicationSaveDTO saveDTO);

    /**
     * 撤回申报
     */
    void withdrawApplication(Long id);

    /**
     * 审核申报
     */
    void auditApplication(Long id, AuditDTO auditDTO);

    /**
     * 获取申报详情
     */
    ApplicationDetailVO getApplicationDetail(Long id);

    /**
     * 分页查询申报（企业端）
     */
    PageResult<ApplicationVO> pageEnterpriseApplications(Long enterpriseId, ApplicationQueryDTO queryDTO);

    /**
     * 分页查询申报（管理端）
     */
    PageResult<ApplicationVO> pageAllApplications(ApplicationQueryDTO queryDTO);

    /**
     * 获取待审核数量
     */
    int countPendingAudit(Integer auditNode);
}
