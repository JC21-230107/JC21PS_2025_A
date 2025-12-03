package jp.co.jc21ps.activity_management.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jp.co.jc21ps.activity_management.entity.ParticipantListEntity;

@Repository
public class ParticipantListRepository {
    private final JdbcTemplate jdbcTemplate;

    public ParticipantListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 初期画面表示
    public List<ParticipantListEntity> getParticipantListData(ParticipantListEntity paramEntity) {
        // 初期表示情報を取得するSQL
        String sql = """
                SELECT 
                    p.activity_id,
                    p.user_id,
                    a.activity_name,
                    u.user_name
                FROM 
                    trn_participant p
                JOIN 
                    trn_activity a
                ON 
                    p.activity_id = a.activity_id
                JOIN 
                    mst_user u
                ON 
                    p.user_id = u.user_id
                WHERE 
                    p.activity_id = ?
                """;

        List<Map<String, Object>> participantList = jdbcTemplate.queryForList(sql,
                paramEntity.getActivityId());

        List<ParticipantListEntity> responseListEntity = new ArrayList<>();

        // リストが空だった場合
        if (participantList.isEmpty()) {
            return responseListEntity;
        }

        for (Map<String, Object> participant : participantList) {

            // responseEntityに値をセットする
            ParticipantListEntity responseEntity = new ParticipantListEntity();
            responseEntity.setActivityId((String) participant.get("activity_id"));
            responseEntity.setUserId((String) participant.get("user_id"));
            responseEntity.setActivityName((String) participant.get("activity_name"));
            responseEntity.setUserName((String) participant.get("user_name"));
            responseListEntity.add(responseEntity);

        }

        return responseListEntity;

    }

    // 活動名を表示
    public String getActivityName(ParticipantListEntity paramEntity) {
        // 活動名を取得するSQL
        String sql = """
                SELECT 
                    activity_name
                FROM 
                    trn_activity
                WHERE 
                    activity_id = ?
                """;

        List<Map<String, Object>> actNameList = jdbcTemplate.queryForList(sql, paramEntity.getActivityId());

        // リストが空だった場合
        if (actNameList.isEmpty()) {
            return "";
        }

        Map<String, Object> responseActName = actNameList.get(0);
        return ((String) responseActName.get("activity_name"));
    }
}
