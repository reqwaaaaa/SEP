package com.pidu.system.controller;

import com.pidu.auth.annotation.RequireLogin;
import com.pidu.common.result.Result;
import com.pidu.common.util.SecurityContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 考试控制器
 */
@Slf4j
@Api(tags = "考试管理")
@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final JdbcTemplate jdbcTemplate;

    @ApiOperation("获取考试列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer status) {
        StringBuilder sql = new StringBuilder(
            "SELECT e.*, c.course_name, " +
            "(SELECT COUNT(*) FROM learn_user_exam WHERE exam_id = e.id) as exam_count, " +
            "(SELECT AVG(score) FROM learn_user_exam WHERE exam_id = e.id AND status = 2) as avg_score, " +
            "(SELECT COUNT(*) FROM learn_user_exam WHERE exam_id = e.id AND is_pass = 1) as pass_count " +
            "FROM learn_exam e " +
            "LEFT JOIN learn_course c ON e.course_id = c.id " +
            "WHERE e.deleted = 0 ");
        
        List<Object> params = new ArrayList<>();
        if (courseId != null) {
            sql.append("AND e.course_id = ? ");
            params.add(courseId);
        }
        if (status != null) {
            sql.append("AND e.status = ? ");
            params.add(status);
        }
        sql.append("ORDER BY e.create_time DESC");
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return Result.success(list);
    }

    @ApiOperation("获取考试详情(含题目)")
    @GetMapping("/{id}")
    @RequireLogin
    public Result<Map<String, Object>> getById(@PathVariable Long id) {
        // 获取考试信息
        String examSql = "SELECT e.*, c.course_name FROM learn_exam e " +
                "LEFT JOIN learn_course c ON e.course_id = c.id WHERE e.id = ?";
        List<Map<String, Object>> examList = jdbcTemplate.queryForList(examSql, id);
        if (examList.isEmpty()) {
            return Result.fail("考试不存在");
        }
        Map<String, Object> exam = new HashMap<>(examList.get(0));
        
        // 获取题目列表
        String questionSql = "SELECT * FROM learn_exam_question WHERE exam_id = ? AND deleted = 0 ORDER BY sort";
        List<Map<String, Object>> questions = jdbcTemplate.queryForList(questionSql, id);
        exam.put("questions", questions);
        
        return Result.success(exam);
    }

    @ApiOperation("创建/更新考试(仅讲师/管理员)")
    @PostMapping("/save")
    @RequireLogin
    public Result<Long> save(@RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        Integer userType = SecurityContextUtil.getCurrentUserType();
        
        // 权限检查：只有讲师(5)和管理员(6)可以创建考试
        if (userType != 5 && userType != 6) {
            return Result.fail("您没有权限创建考试");
        }
        
        Long id = data.get("id") != null ? Long.valueOf(data.get("id").toString()) : null;
        
        if (id != null) {
            String sql = "UPDATE learn_exam SET exam_name=?, course_id=?, description=?, duration=?, " +
                    "total_score=?, pass_score=?, start_time=?, end_time=?, allow_times=?, " +
                    "show_answer=?, status=?, update_by=?, update_time=NOW() WHERE id=?";
            jdbcTemplate.update(sql,
                data.get("examName"), data.get("courseId"), data.get("description"), data.get("duration"),
                data.get("totalScore"), data.get("passScore"), data.get("startTime"), data.get("endTime"),
                data.get("allowTimes"), data.get("showAnswer"), data.get("status"), userId, id);
        } else {
            id = System.currentTimeMillis();
            String sql = "INSERT INTO learn_exam (id, exam_name, course_id, description, duration, " +
                    "total_score, pass_score, start_time, end_time, allow_times, show_answer, status, " +
                    "create_by, create_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,NOW())";
            jdbcTemplate.update(sql, id,
                data.get("examName"), data.get("courseId"), data.get("description"), data.get("duration"),
                data.get("totalScore"), data.get("passScore"), data.get("startTime"), data.get("endTime"),
                data.get("allowTimes"), data.get("showAnswer"), data.get("status"), userId);
        }
        
        // 保存题目
        List<Map<String, Object>> questions = (List<Map<String, Object>>) data.get("questions");
        if (questions != null && !questions.isEmpty()) {
            // 先删除旧题目
            jdbcTemplate.update("DELETE FROM learn_exam_question WHERE exam_id = ?", id);
            
            // 插入新题目
            int sort = 1;
            for (Map<String, Object> q : questions) {
                long qId = System.currentTimeMillis() + sort;
                String qSql = "INSERT INTO learn_exam_question (id, exam_id, question_type, question_content, " +
                        "options, correct_answer, analysis, score, sort, create_by, create_time) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,NOW())";
                jdbcTemplate.update(qSql, qId, id, q.get("questionType"), q.get("questionContent"),
                        q.get("options"), q.get("correctAnswer"), q.get("analysis"), q.get("score"), sort++, userId);
            }
            
            // 更新题目数量
            jdbcTemplate.update("UPDATE learn_exam SET question_count = ? WHERE id = ?", questions.size(), id);
        }
        
        return Result.success(id);
    }

    @ApiOperation("开始考试")
    @PostMapping("/start/{examId}")
    @RequireLogin
    public Result<Long> startExam(@PathVariable Long examId) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        
        // 检查考试是否存在且可参加
        String examSql = "SELECT * FROM learn_exam WHERE id = ? AND deleted = 0";
        List<Map<String, Object>> examList = jdbcTemplate.queryForList(examSql, examId);
        if (examList.isEmpty()) {
            return Result.fail("考试不存在");
        }
        Map<String, Object> exam = examList.get(0);
        
        Integer status = (Integer) exam.get("status");
        if (status != 1) {
            return Result.fail("考试未开放");
        }
        
        // 检查是否已有进行中的考试记录
        String checkSql = "SELECT id FROM learn_user_exam WHERE user_id = ? AND exam_id = ? AND status = 0";
        List<Map<String, Object>> existing = jdbcTemplate.queryForList(checkSql, userId, examId);
        if (!existing.isEmpty()) {
            return Result.success(Long.valueOf(existing.get(0).get("id").toString()));
        }
        
        // 检查考试次数限制
        Integer allowTimes = (Integer) exam.get("allow_times");
        if (allowTimes != null && allowTimes > 0) {
            String countSql = "SELECT COUNT(*) as cnt FROM learn_user_exam WHERE user_id = ? AND exam_id = ?";
            Integer count = ((Number) jdbcTemplate.queryForMap(countSql, userId, examId).get("cnt")).intValue();
            if (count >= allowTimes) {
                return Result.fail("已达到最大考试次数");
            }
        }
        
        // 创建考试记录
        long recordId = System.currentTimeMillis();
        String insertSql = "INSERT INTO learn_user_exam (id, user_id, exam_id, start_time, status, create_by, create_time) " +
                "VALUES (?,?,?,NOW(),0,?,NOW())";
        jdbcTemplate.update(insertSql, recordId, userId, examId, userId);
        
        return Result.success(recordId);
    }

    @ApiOperation("提交考试答案")
    @PostMapping("/submit/{recordId}")
    @RequireLogin
    public Result<Map<String, Object>> submitExam(@PathVariable Long recordId, @RequestBody Map<String, Object> data) {
        Long userId = SecurityContextUtil.getCurrentUserId();
        
        // 获取考试记录
        String recordSql = "SELECT ue.*, e.pass_score, e.show_answer FROM learn_user_exam ue " +
                "LEFT JOIN learn_exam e ON ue.exam_id = e.id WHERE ue.id = ? AND ue.user_id = ?";
        List<Map<String, Object>> recordList = jdbcTemplate.queryForList(recordSql, recordId, userId);
        if (recordList.isEmpty()) {
            return Result.fail("考试记录不存在");
        }
        Map<String, Object> record = recordList.get(0);
        
        if (((Integer) record.get("status")) != 0) {
            return Result.fail("考试已提交");
        }
        
        Long examId = Long.valueOf(record.get("exam_id").toString());
        Integer passScore = (Integer) record.get("pass_score");
        
        // 获取题目和正确答案
        String questionSql = "SELECT id, correct_answer, score FROM learn_exam_question WHERE exam_id = ? AND deleted = 0";
        List<Map<String, Object>> questions = jdbcTemplate.queryForList(questionSql, examId);
        
        // 计算得分
        Map<String, String> answers = (Map<String, String>) data.get("answers");
        int totalScore = 0;
        
        for (Map<String, Object> q : questions) {
            String qId = q.get("id").toString();
            String correctAnswer = (String) q.get("correct_answer");
            Integer score = (Integer) q.get("score");
            
            String userAnswer = answers != null ? answers.get(qId) : null;
            if (userAnswer != null && userAnswer.equals(correctAnswer)) {
                totalScore += score;
            }
        }
        
        // 判断是否及格
        boolean isPass = totalScore >= passScore;
        
        // 更新考试记录
        String answersJson = answers != null ? answers.toString() : "{}";
        String updateSql = "UPDATE learn_user_exam SET submit_time = NOW(), score = ?, is_pass = ?, " +
                "answers = ?, status = 1 WHERE id = ?";
        jdbcTemplate.update(updateSql, totalScore, isPass ? 1 : 0, answersJson, recordId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("score", totalScore);
        result.put("isPass", isPass);
        result.put("passScore", passScore);
        
        return Result.success(result);
    }

    @ApiOperation("获取我的考试记录")
    @GetMapping("/my-records")
    @RequireLogin
    public Result<List<Map<String, Object>>> getMyRecords() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String sql = "SELECT ue.*, e.exam_name, e.total_score, e.pass_score, c.course_name " +
                "FROM learn_user_exam ue " +
                "LEFT JOIN learn_exam e ON ue.exam_id = e.id " +
                "LEFT JOIN learn_course c ON e.course_id = c.id " +
                "WHERE ue.user_id = ? ORDER BY ue.create_time DESC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, userId);
        return Result.success(list);
    }

    @ApiOperation("获取考试统计(讲师/管理员)")
    @GetMapping("/statistics/{examId}")
    @RequireLogin
    public Result<Map<String, Object>> getStatistics(@PathVariable Long examId) {
        Integer userType = SecurityContextUtil.getCurrentUserType();
        if (userType != 5 && userType != 6) {
            return Result.fail("您没有权限查看统计");
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // 参考人数
        String countSql = "SELECT COUNT(*) as total FROM learn_user_exam WHERE exam_id = ?";
        Integer totalCount = ((Number) jdbcTemplate.queryForMap(countSql, examId).get("total")).intValue();
        stats.put("totalCount", totalCount);
        
        // 已提交人数
        String submitSql = "SELECT COUNT(*) as total FROM learn_user_exam WHERE exam_id = ? AND status >= 1";
        stats.put("submitCount", jdbcTemplate.queryForMap(submitSql, examId).get("total"));
        
        // 及格人数
        String passSql = "SELECT COUNT(*) as total FROM learn_user_exam WHERE exam_id = ? AND is_pass = 1";
        Integer passCount = ((Number) jdbcTemplate.queryForMap(passSql, examId).get("total")).intValue();
        stats.put("passCount", passCount);
        
        // 及格率
        stats.put("passRate", totalCount > 0 ? Math.round(passCount * 100.0 / totalCount) : 0);
        
        // 平均分
        String avgSql = "SELECT COALESCE(AVG(score), 0) as avg_score FROM learn_user_exam WHERE exam_id = ? AND status >= 1";
        Number avgScore = (Number) jdbcTemplate.queryForMap(avgSql, examId).get("avg_score");
        stats.put("avgScore", avgScore != null ? Math.round(avgScore.doubleValue()) : 0);
        
        // 最高分
        String maxSql = "SELECT COALESCE(MAX(score), 0) as max_score FROM learn_user_exam WHERE exam_id = ? AND status >= 1";
        stats.put("maxScore", jdbcTemplate.queryForMap(maxSql, examId).get("max_score"));
        
        // 最低分
        String minSql = "SELECT COALESCE(MIN(score), 0) as min_score FROM learn_user_exam WHERE exam_id = ? AND status >= 1";
        stats.put("minScore", jdbcTemplate.queryForMap(minSql, examId).get("min_score"));
        
        // 分数分布 - 返回前端需要的格式
        String distSql = "SELECT " +
                "SUM(CASE WHEN score < 60 THEN 1 ELSE 0 END) as fail_count, " +
                "SUM(CASE WHEN score >= 60 AND score < 70 THEN 1 ELSE 0 END) as d_count, " +
                "SUM(CASE WHEN score >= 70 AND score < 80 THEN 1 ELSE 0 END) as c_count, " +
                "SUM(CASE WHEN score >= 80 AND score < 90 THEN 1 ELSE 0 END) as b_count, " +
                "SUM(CASE WHEN score >= 90 THEN 1 ELSE 0 END) as a_count " +
                "FROM learn_user_exam WHERE exam_id = ? AND status >= 1";
        Map<String, Object> dist = jdbcTemplate.queryForMap(distSql, examId);
        
        int failCount = dist.get("fail_count") != null ? ((Number) dist.get("fail_count")).intValue() : 0;
        int dCount = dist.get("d_count") != null ? ((Number) dist.get("d_count")).intValue() : 0;
        int cCount = dist.get("c_count") != null ? ((Number) dist.get("c_count")).intValue() : 0;
        int bCount = dist.get("b_count") != null ? ((Number) dist.get("b_count")).intValue() : 0;
        int aCount = dist.get("a_count") != null ? ((Number) dist.get("a_count")).intValue() : 0;
        int total = failCount + dCount + cCount + bCount + aCount;
        
        List<Map<String, Object>> distribution = new ArrayList<>();
        distribution.add(createDistItem("0-59", failCount, total, "#F56C6C"));
        distribution.add(createDistItem("60-69", dCount, total, "#E6A23C"));
        distribution.add(createDistItem("70-79", cCount, total, "#409EFF"));
        distribution.add(createDistItem("80-89", bCount, total, "#67C23A"));
        distribution.add(createDistItem("90-100", aCount, total, "#9b59b6"));
        stats.put("distribution", distribution);
        
        return Result.success(stats);
    }
    
    private Map<String, Object> createDistItem(String range, int count, int total, String color) {
        Map<String, Object> item = new HashMap<>();
        item.put("range", range);
        item.put("count", count);
        item.put("percent", total > 0 ? Math.round(count * 100.0 / total) : 0);
        item.put("color", color);
        return item;
    }

    @ApiOperation("获取考试成绩列表(讲师/管理员)")
    @GetMapping("/scores/{examId}")
    @RequireLogin
    public Result<List<Map<String, Object>>> getScores(@PathVariable Long examId) {
        Integer userType = SecurityContextUtil.getCurrentUserType();
        if (userType != 5 && userType != 6) {
            return Result.fail("您没有权限查看成绩");
        }
        
        String sql = "SELECT ue.*, u.real_name, u.username FROM learn_user_exam ue " +
                "LEFT JOIN sys_user u ON ue.user_id = u.id " +
                "WHERE ue.exam_id = ? ORDER BY ue.score DESC, ue.submit_time ASC";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, examId);
        return Result.success(list);
    }

    @ApiOperation("删除考试")
    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<Void> delete(@PathVariable Long id) {
        Integer userType = SecurityContextUtil.getCurrentUserType();
        if (userType != 5 && userType != 6) {
            return Result.fail("您没有权限删除考试");
        }
        jdbcTemplate.update("UPDATE learn_exam SET deleted = 1 WHERE id = ?", id);
        return Result.success();
    }
}
