package jp.co.jc21ps.activity_management.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.context.MessageSource;
import jp.co.jc21ps.activity_management.dto.ParticipantListDto;
import jp.co.jc21ps.activity_management.form.ParticipantListForm;
import jp.co.jc21ps.activity_management.service.CommonService;
import jp.co.jc21ps.activity_management.service.ParticipantListService;
import jp.co.jc21ps.activity_management.dto.ParticipantDto;
import jp.co.jc21ps.activity_management.dto.SessionDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/participantList")
public class ParticipantListController {

    private final ParticipantListService participantListService;
    private final CommonService commonService;
    private final MessageSource messageSource;

    public ParticipantListController(ParticipantListService participantListService, CommonService commonService,
            MessageSource messageSource) {
        this.participantListService = participantListService;
        this.commonService = commonService;
        this.messageSource = messageSource;
    }

    /**
     * @param activityId
     * @param session
     * @return
     */
    @GetMapping
    public ModelAndView dispParticipantList(@RequestParam(value = "activityId", required = true) String activityId,
            HttpSession session) {

        ModelAndView mav = new ModelAndView();

        // 活動IDが存在しない場合、エラー画面に遷移
        if (activityId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // セッションからuserId, clubIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();
        String clubId = sessionDto.getClubId();

        // セッションが切れた場合、エラー画面に遷移
        if (userId == null || userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // dtoに値をセット
        ParticipantListDto dto = new ParticipantListDto();
        dto.setUserId(userId);
        dto.setActivityId(activityId);
        

        try {
            // participantListServiceのgetParticipantListDataメソッドを呼び出す
            ParticipantDto responseDto = participantListService.getParticipantListData(dto);

            // 返却用のリスト
            List<ParticipantListForm> responseListForm = new ArrayList<>();

            // responseListFormに値をセット
            if (responseDto.getPariticipantListDto() != null) {
                for (ParticipantListDto participantDto : responseDto.getPariticipantListDto()) {
                    ParticipantListForm form = new ParticipantListForm();
                    form.setActivityId(participantDto.getActivityId());
                    form.setUserId(participantDto.getUserId());
                    form.setActivityName(participantDto.getActivityName());
                    form.setUserName(participantDto.getUserName());
                    responseListForm.add(form);
                }
            }

            // 取得したデータを画面側に渡す
            mav.addObject("participantList", responseListForm);
            mav.addObject("activityName", responseDto.getActivityName());


            // messages.propertiesからメッセージを取得
            String resultMessage = messageSource.getMessage("notpariticipant", null, Locale.getDefault());
            mav.addObject("message", resultMessage);
            // セッションから取得したclubIdを設定
            mav.addObject("leaderClubId", clubId);

            // 遷移先の設定
            mav.setViewName("participantList");
        } catch (Exception e) {
            mav.setViewName("error");
        }

        return mav;

    }

}