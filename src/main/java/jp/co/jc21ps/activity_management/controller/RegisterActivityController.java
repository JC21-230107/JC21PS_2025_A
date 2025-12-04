package jp.co.jc21ps.activity_management.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import jp.co.jc21ps.activity_management.dto.SessionDto;
import jp.co.jc21ps.activity_management.form.RegisterActivitySaveForm;
import jp.co.jc21ps.activity_management.service.CommonService;
import jp.co.jc21ps.activity_management.service.RegisterActivityService;
import jp.co.jc21ps.activity_management.dto.RegisterActivityDto;
import jp.co.jc21ps.activity_management.dto.RegisterActivitySaveDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registerActivity")
public class RegisterActivityController {

    private final RegisterActivityService registerActivityService;
    private final MessageSource messageSource;
    private final CommonService commonService;

    public RegisterActivityController(RegisterActivityService registerActivityService, MessageSource messageSource,
            CommonService commonService) {
        this.registerActivityService = registerActivityService;
        this.commonService = commonService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ModelAndView getActivity(HttpSession session, RegisterActivitySaveForm paramForm) {

        ModelAndView mav = new ModelAndView();

        // セッションからuserId, clubIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();
        String leaderClubId = sessionDto.getClubId();

        // セッションが切れた場合、エラー画面に遷移
        if (userId == null || userId.isEmpty() || leaderClubId == null || leaderClubId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // paramFormに値をセット
        paramForm.setClubId(leaderClubId);

        // dtoに値をセット
        RegisterActivityDto activityDto = new RegisterActivityDto();
        activityDto.setClubId(leaderClubId);

        RegisterActivityDto registerActivityDto = registerActivityService.findActivity(activityDto);

        // responseFormに値をセット
        RegisterActivitySaveForm responseForm = new RegisterActivitySaveForm();
        responseForm.setClubName(registerActivityDto.getClubName());

        // 活動登録画面に遷移
        mav.addObject("registerActivitySaveForm", responseForm);
        mav.addObject("leaderClubId", leaderClubId);
        mav.setViewName("registerActivity");
        return mav;
    }

    @PostMapping("/save")
    public ModelAndView insertActivity(@Valid RegisterActivitySaveForm paramForm,
            BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {

        ModelAndView mav = new ModelAndView();

        // セッションからuserId, clubIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();
        String leaderClubId = sessionDto.getClubId();

        // バリデーションエラー
        if (bindingResult.hasErrors()) {
            mav.addObject("registerActivitySaveForm", paramForm);
            mav.addObject("leaderClubId", leaderClubId);
            mav.setViewName("registerActivity");
            return mav;
        }

        // セッションが切れた場合、エラー画面に遷移
        if (leaderClubId == null || leaderClubId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        try {
            // インスタンス化
            RegisterActivitySaveDto activitySaveDto = new RegisterActivitySaveDto();

            // activitySaveDtoに、パラメータをsetする
            activitySaveDto.setActivityName(paramForm.getActivityName());
            activitySaveDto.setActivityDate(paramForm.getActivityDate());
            activitySaveDto.setActivityPlace(paramForm.getActivityPlace());
            activitySaveDto.setActivityStartTime(paramForm.getActivityStartTime());
            activitySaveDto.setActivityEndTime(paramForm.getActivityEndTime());
            activitySaveDto.setActivityDescription(paramForm.getActivityDescription());
            activitySaveDto.setMaxParticipant(paramForm.getMaxParticipant());
            activitySaveDto.setClubId(leaderClubId);

            // サービスからinsertメソッドを呼び出す
            String resultMessageKey = registerActivityService.insertActivity(activitySaveDto);

            // messages.propertiesからメッセージを取得
            String resultMessage = messageSource.getMessage(resultMessageKey, null,
                    Locale.getDefault());

            // 活動登録に成功した場合、トップ画面に遷移
            if ("activityRegisterCompleteMessage".equals(resultMessageKey)) {
                redirectAttributes.addFlashAttribute("activityRegisterCompleteMessage", resultMessage);
                mav.setViewName("redirect:/top");
                return mav;
            } else {
                // 活動登録に失敗した場合、エラー画面に遷移
                mav.setViewName("error");
            }

        } catch (Exception e) {
            // DB接続に失敗した場合、エラー画面に遷移
            mav.setViewName("error");
        }
        return mav;
    }
}