package jp.co.jc21ps.activity_management.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import jp.co.jc21ps.activity_management.dto.JoinRequestDto;
import jp.co.jc21ps.activity_management.dto.JoinRequestSaveDto;
import jp.co.jc21ps.activity_management.dto.SessionDto;
import jp.co.jc21ps.activity_management.form.JoinRequestSaveForm;
import jp.co.jc21ps.activity_management.service.CommonService;
import jp.co.jc21ps.activity_management.service.JoinRequestService;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/joinRequest")

public class JoinRequestController {

    private final JoinRequestService joinRequestService;
    private final MessageSource messageSource;
    private final CommonService commonService;

    // サービスをセット
    public JoinRequestController(JoinRequestService joinRequestService, MessageSource messageSource,
            CommonService commonService) {
        this.joinRequestService = joinRequestService;
        this.messageSource = messageSource;
        this.commonService = commonService;
    }

    @GetMapping
    public ModelAndView getJoinRequestById(HttpSession session, JoinRequestSaveForm paramForm,
            @ModelAttribute("joinOkMessage") String joinOkMessage) {

        ModelAndView mav = new ModelAndView();

        // セッションからuserIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();
        String leaderClubId = sessionDto.getClubId();

        // セッションが切れた場合、エラー画面に遷移
        if (userId == null || userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // formに値をセット
        JoinRequestSaveForm form = new JoinRequestSaveForm();
        form.setUserId(userId);

        // dtoに値をセット
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUserId(userId);

        List<JoinRequestDto> joinRequestList = joinRequestService.findRequest(joinRequestDto);
        List<JoinRequestSaveForm> responseForm = new ArrayList<>();

        // formに値をセット
        for (JoinRequestDto dto : joinRequestList) {

            JoinRequestSaveForm saveData = new JoinRequestSaveForm();
            saveData.setClubName(dto.getClubName());
            saveData.setClubDescription(dto.getClubDescription());
            saveData.setClubId(dto.getClubId());

            // responseFormにリストを追加
            responseForm.add(saveData);

        }
        // 初期表示情報取得結果に応じて、条件分岐処理
        // 成功メッセージをオブジェクトに追加
        if (joinOkMessage != null && !joinOkMessage.isEmpty()) {
            mav.addObject("joinRequestCompleteMessage", joinOkMessage);
        }
        
        // リストが空の場合、メッセージを表示
        if (responseForm.isEmpty()) {
            String notRequestClubMessage = messageSource.getMessage("notRequestClubMessage", null, Locale.getDefault());
            mav.addObject("notRequestClubMessage", notRequestClubMessage);
        } else {
            // レスポンスをオブジェクトに追加（club_name, club_description, club_id）
            mav.addObject("clubList", responseForm);
        }

        mav.addObject("leaderClubId", leaderClubId);

        // 部員登録申請画面に遷移
        mav.setViewName("joinRequest");
        return mav;

    }

    // インサート処理
    @PostMapping("/save")
    public ModelAndView insertRequestClub(HttpSession session, JoinRequestSaveForm paramForm,
            RedirectAttributes redirectAttributes) {

        ModelAndView mav = new ModelAndView();

        // セッションからuserIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();

        // セッションが切れた場合、エラー画面に遷移
        if (userId == null || userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // dtoに値をセット
        JoinRequestSaveDto joinRequestSaveDto = new JoinRequestSaveDto();
        joinRequestSaveDto.setUserId(userId);
        joinRequestSaveDto.setClubId(paramForm.getClubId());

        try {
            boolean result = joinRequestService.insertJoinRequest(joinRequestSaveDto);
            // インサートの成功、失敗に応じて、処理を変更する
            if (result) {
                // 成功時：メッセージを取得してリダイレクト属性に追加
                String joinRequestCompleteMessage = messageSource.getMessage("joinRequestCompleteMessage", null, Locale.getDefault());
                redirectAttributes.addFlashAttribute("joinOkMessage", joinRequestCompleteMessage);
                // 部員登録申請画面へリダイレクト
                mav.setViewName("redirect:/joinRequest");
                return mav;
            } else {
                // 失敗時：エラー画面に遷移
                mav.setViewName("error");
            }

        } catch (Exception e) {
            // 例外発生時：エラー画面に遷移
>>>>>>> cd18e4737bf8716ac2515203127aceee4d9ab0bf
            mav.setViewName("error");
        }
        return mav;
    }
}
