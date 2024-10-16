package com.example.sinitto.callback.controller;

import com.example.sinitto.callback.dto.CallbackResponse;
import com.example.sinitto.callback.dto.CallbackUsageHistoryResponse;
import com.example.sinitto.callback.service.CallbackService;
import com.example.sinitto.common.annotation.MemberId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/callbacks")
@Tag(name = "콜백 서비스", description = "콜백 관련 API")
public class CallbackController {

    private final CallbackService callbackService;

    public CallbackController(CallbackService callbackService) {
        this.callbackService = callbackService;
    }

    @Operation(summary = "콜백 전화 리스트 보기(페이지)", description = "시니어가 요청한 콜백전화를 페이징으로 보여줍니다.")
    @GetMapping
    public ResponseEntity<Page<CallbackResponse>> getWaitingCallbackList(@MemberId Long memberId,
                                                                         @PageableDefault(sort = "postTime", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(callbackService.getWaitingCallbacks(memberId, pageable));
    }

    @Operation(summary = "진행 상태인 콜백을 완료 대기 상태로 전환(시니또가)", description = "시니또가 수락한 콜백 수행을 완료했을때 이 api 호출하면 완료 대기 상태로 변합니다.")
    @PutMapping("/pendingComplete/{callbackId}")
    public ResponseEntity<Void> pendingCompleteCallback(@MemberId Long memberId,
                                                        @PathVariable Long callbackId) {

        callbackService.changeCallbackStatusToPendingCompleteBySinitto(memberId, callbackId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "완료 대기 상태인 콜백을 완료 상태로 전환(보호자가)", description = "보호자가 완료 대기 상태인 콜백을 완료 확정 시킵니다.")
    @PutMapping("/complete/{callbackId}")
    public ResponseEntity<Void> completeCallback(@MemberId Long memberId,
                                                 @PathVariable Long callbackId) {

        callbackService.changeCallbackStatusToCompleteByGuard(memberId, callbackId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "콜백 서비스 수락 신청", description = "시니또가 콜백 서비스 수락을 신청합니다.")
    @PutMapping("/accept/{callbackId}")
    public ResponseEntity<Void> acceptCallback(@MemberId Long memberId,
                                               @PathVariable Long callbackId) {

        callbackService.acceptCallbackBySinitto(memberId, callbackId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "진행중인 콜백 서비스 취소", description = "시니또가 진행중인 콜백 서비스를 취소합니다. 콜백은 다시 대기 상태로 돌아갑니다.")
    @PutMapping("/cancel/{callbackId}")
    public ResponseEntity<Void> cancelCallback(@MemberId Long memberId,
                                               @PathVariable Long callbackId) {

        callbackService.cancelCallbackAssignmentBySinitto(memberId, callbackId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/twilio")
    public ResponseEntity<String> addCallCheck(@RequestParam("From") String fromNumber) {

        return ResponseEntity.ok(callbackService.createCallbackByCall(fromNumber));
    }

    @Operation(summary = "시니또에게 현재 할당된 콜백 조회", description = "현재 시니또 본인에게 할당된 콜백을 조회합니다.")
    @GetMapping("/sinitto/accepted")
    public ResponseEntity<CallbackResponse> getAcceptedCallback(@MemberId Long memberId) {

        return ResponseEntity.ok(callbackService.getAcceptedCallback(memberId));
    }

    @Operation(summary = "보호자의 콜백 이용 내역 조회", description = "보호자에게 연관된 모든 콜백 내역을 조회합니다. 기본적으로 최신 내역 부터 조회됩니다.")
    @GetMapping("/guard/requested")
    public ResponseEntity<Page<CallbackUsageHistoryResponse>> getAcceptedCallback(@MemberId Long memberId,
                                                                                  @PageableDefault(sort = "postTime", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(callbackService.getCallbackHistoryOfGuard(memberId, pageable));
    }

    @Operation(summary = "콜백 단건 조회", description = "콜백 id 로 콜백을 단건 조회합니다.")
    @GetMapping("/{callbackId}")
    public ResponseEntity<CallbackResponse> getCallback(@PathVariable("callbackId") Long callbackId) {

        return ResponseEntity.ok(callbackService.getCallback(callbackId));
    }

}
