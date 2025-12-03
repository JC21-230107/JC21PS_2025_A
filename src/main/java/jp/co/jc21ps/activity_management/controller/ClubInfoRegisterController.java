package jp.co.jc21ps.activity_management.controller;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import jp.co.jc21ps.activity_management.dto.ClubInfoRegisterDto;
import jp.co.jc21ps.activity_management.dto.SessionDto;
import jp.co.jc21ps.activity_management.form.ClubInfoRegisterSaveForm;
import jp.co.jc21ps.activity_management.service.ClubInfoRegisterService;
import jp.co.jc21ps.activity_management.service.CommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/clubInfoRegister")

public class ClubInfoRegisterController {

    private final ClubInfoRegisterService clubInfoRegisterService;
    private final CommonService commonService;
    private final MessageSource messageSource;

    public ClubInfoRegisterController(ClubInfoRegisterService clubInfoRegisterService,
            MessageSource messageSource, CommonService commonService) {

        this.clubInfoRegisterService = clubInfoRegisterService;
        this.commonService = commonService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ModelAndView getClubInfo(HttpSession session, ClubInfoRegisterSaveForm paramForm) {

        ModelAndView mav = new ModelAndView();

        // セッションからclubIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String leaderClubId = sessionDto.getClubId();

        // セッションが切れた場合、エラー画面に遷移
        if (leaderClubId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        ClubInfoRegisterDto dto = new ClubInfoRegisterDto();
        dto.setLeaderClubId(leaderClubId);

        try {
            ClubInfoRegisterDto clubInfoRegisterDto = clubInfoRegisterService.getClubInfoByClubId(dto);

            // responseformに値をセット
            ClubInfoRegisterSaveForm responseForm = new ClubInfoRegisterSaveForm();
            responseForm.setClubName(clubInfoRegisterDto.getClubName());
            responseForm.setClubDescription(clubInfoRegisterDto.getClubDescription());
            responseForm.setLeaderClubId(clubInfoRegisterDto.getLeaderClubId());

            mav.addObject("leaderClubId", leaderClubId);
            mav.addObject("clubInfoRegisterSaveForm", responseForm);

            // 部署情報登録画面に遷移
            mav.setViewName("clubInfoRegister");

        } catch (Exception e) {
            // DB接続に失敗した場合、エラー画面に遷移
            mav.setViewName("error");
        }

        return mav;
    }

    @PostMapping("/save")
    public ModelAndView updateClubInfo(@Valid ClubInfoRegisterSaveForm paramForm,
            BindingResult bindingResult, HttpSession session) {

        ModelAndView mav = new ModelAndView();

        // セッションからClubIdを取得
        SessionDto sessionDto = commonService.getSessionDto(session);
        String leaderClubId = sessionDto.getClubId();

        // バリデーション
        if (bindingResult.hasErrors()) {
            paramForm.setLeaderClubId(leaderClubId);
            mav.addObject("clubInfoRegisterSaveForm", paramForm);
            mav.setViewName("clubInfoRegister");
            return mav;
        }

        // セッションが切れた場合、エラー画面に遷移
        if (leaderClubId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        try {
            ClubInfoRegisterDto clubInfoRegisterDto = new ClubInfoRegisterDto();
            clubInfoRegisterDto.setLeaderClubId(leaderClubId);
            clubInfoRegisterDto.setClubDescription(paramForm.getClubDescription());

            String result = clubInfoRegisterService.updateClubInfo(clubInfoRegisterDto);

            // messages.propertiesからメッセージを取得
            String resultMessage = messageSource.getMessage(result, null, Locale.getDefault());

            if ("updateClubInfo".equals(result)) {
                // 更新成功時
                mav.addObject("message", resultMessage);
                paramForm.setLeaderClubId(leaderClubId);
                mav.addObject("clubInfoRegisterSaveForm", paramForm);
                mav.setViewName("clubInfoRegister");
            } else {
                // 更新失敗時
                mav.setViewName("error");
            }

        } catch (Exception e) {
            // DB接続失敗した場合、エラー画面に遷移
            mav.setViewName("error");
        }

        return mav;
    }

}
