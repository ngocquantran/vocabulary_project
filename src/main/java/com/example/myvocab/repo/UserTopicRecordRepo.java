package com.example.myvocab.repo;

import com.example.myvocab.model.UserTopicRecord;
import com.example.myvocab.model.enummodel.LearningStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopicRecordRepo extends JpaRepository<UserTopicRecord, Long> {
    Optional<UserTopicRecord> findByStageAndUserTopic_Id(LearningStage stage, Long id);

    List<UserTopicRecord> findByUserTopic_Id(Long id);

    @Query(
            value = "SELECT *  " +
                    " FROM user_topic_record utr " +
                    " INNER JOIN user_topic ut ON utr.id_user_topic=ut.id " +
                    " WHERE utr.stage ='NOW' AND ut.id_topic= ?1 "+
                    " ORDER BY utr.rightAnswers DESC, utr.testTime ASC, utr.id ASC"+
                    "  LIMIT 8",
            nativeQuery = true)
    List<UserTopicRecord> getTopEightOfUserTopic(Long topicId);



    @Query(
            value = "SELECT *  " +
                    " FROM user_topic_record utr " +
                    " INNER JOIN user_topic ut ON utr.id_user_topic=ut.id " +
                    " WHERE utr.stage ='NOW' AND ut.id_topic= ?1 "+
                    " ORDER BY utr.rightAnswers DESC, utr.testTime ASC, utr.id ASC",
            nativeQuery = true)
    List<UserTopicRecord> getTopRankOfUserTopic(Long topicId);




//    @Query(
//            value = "WITH rank_chart AS( " +
//                                        " SELECT utr ,ROW_NUMBER () OVER(PARTITION BY utr.id_user_topic ORDER BY utr.rightAnswers DESC,utr.testTime ASC,utr.id ASC ) as user_rank  " +
//                                        " FROM user_topic_record utr " +
//                                        " INNER JOIN user_topic ut ON utr.id_user_topic=ut.id " +
//                                        " WHERE utr.stage ='NOW' AND ut.id_topic= ?1 )"  +
//                    "SELECT rc.user_rank " +
//                    "FROM rank_chart rc " +
//                    "WHERE rc.user_id =?2",
//            nativeQuery = true)
//    Optional<Integer> getUserTopicRank(Long topicId,String userId);





}