package jp.co.jc21ps.activity_management.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jp.co.jc21ps.activity_management.entity.JoinRequestEntity;
import jp.co.jc21ps.activity_management.entity.JoinRequestSaveEntity;

@Repository
public class JoinRequestRepository {
    private final JdbcTemplate jdbcTemplate;

    public JoinRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 初期画面表示
    public List<JoinRequestEntity> getJoinRequestById(JoinRequestEntity paramEntity) {
        // 初期表示情報を取得するSQL
        // mst_clubから、trn_join_requestとtrn_club_memberに存在しないclubを取得
        String sql = """
<<<<<<< HEAD
                SELECT 
                    c.club_id,
                    c.club_name,
                    c.club_description
                FROM 
                    mst_club c
                WHERE 
                    NOT EXISTS (
                        SELECT 1 
                        FROM trn_join_request jr 
                        WHERE jr.user_id = ? 
                        AND jr.club_id = c.club_id
                    )
                AND 
                    NOT EXISTS (
                        SELECT 1 
                        FROM trn_club_member cm 
                        WHERE cm.user_id = ? 
                        AND cm.club_id = c.club_id
                    )
                ORDER BY 
                    c.club_id
=======
                SELECT
                    club.club_id,
                    club.club_name,
                    club.club_description
                FROM
                    mst_club as club
                WHERE
                    club.club_id NOT IN (
                        SELECT club_id FROM trn_join_request WHERE user_id = ?
                    )
                    AND club.club_id NOT IN (
                        SELECT club_id FROM trn_club_member WHERE user_id = ?
                    )
                ORDER BY
                    club.club_id ASC
>>>>>>> cd18e4737bf8716ac2515203127aceee4d9ab0bf
                """;

        List<JoinRequestEntity> responseEntity = new ArrayList<>();
        List<Map<String, Object>> joinRequestList = jdbcTemplate.queryForList(sql, paramEntity.getUserId(),
                paramEntity.getUserId());

        // リストが空だった場合
        if (joinRequestList.isEmpty()) {
            return responseEntity;
        }

        for (Map<String, Object> joinRequest : joinRequestList) {

            // entityに値をセットする
            JoinRequestEntity joinData = new JoinRequestEntity();
            joinData.setClubName((String) joinRequest.get("club_name"));
            joinData.setClubDescription((String) joinRequest.get("club_description"));
            joinData.setClubId((String) joinRequest.get("club_id"));
            responseEntity.add(joinData);

        }

        return responseEntity;
    }

    // 申請処理
    public void insertClub(JoinRequestSaveEntity paramEntity) {
        // 申請者の情報をインサートするSQL
        String sql = """
<<<<<<< HEAD
                INSERT INTO 
                    trn_join_request (user_id, club_id, leader_flg)
                VALUES (?, ?, 0)
=======
                INSERT INTO
                    trn_join_request
                    (user_id,
                    club_id)
                VALUES
                    (?, ?)
>>>>>>> cd18e4737bf8716ac2515203127aceee4d9ab0bf
                """;

        // entityから値をゲットする
        Object[] paramList = {
                paramEntity.getUserId(),
                paramEntity.getClubId(),
        };

        jdbcTemplate.update(sql, paramList);
    }
}