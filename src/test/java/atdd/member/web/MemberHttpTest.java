package atdd.member.web;

import atdd.member.application.dto.CreateMemberRequestView;
import atdd.member.application.dto.MemberResponseView;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

public class MemberHttpTest
{
    public static final String MEMBER_URL = "/members";
    public WebTestClient webTestClient;

    public MemberHttpTest(WebTestClient webTestClient)
    {
        this.webTestClient = webTestClient;
    }

    public Long createMember(String email, String name, String password)
    {
        EntityExchangeResult<MemberResponseView> memberResponse = createMemberRequest(email, name, password);
        return memberResponse.getResponseBody().getId();
    }

    public EntityExchangeResult<MemberResponseView> createMemberRequest(String email, String name, String password)
    {
        CreateMemberRequestView createMemberRequestView = new CreateMemberRequestView(email, name, password);
        return webTestClient.post().uri(MEMBER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(createMemberRequestView), CreateMemberRequestView.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectHeader().exists("Location")
                .expectBody(MemberResponseView.class)
                .returnResult();
    }

    public EntityExchangeResult<MemberResponseView> retrieveMemberRequest(String uri) {
        return webTestClient.get().uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MemberResponseView.class)
                .returnResult();
    }

    public EntityExchangeResult<MemberResponseView> retrieveMember(Long id)
    {
        return retrieveMemberRequest(MEMBER_URL + "/" + id);
    }

    public EntityExchangeResult<MemberResponseView> retrieveMyInfo(String email, String password, String accessToken)
    {
        return webTestClient.get().uri(MEMBER_URL + "/me")
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(MemberResponseView.class)
                .returnResult();
    }
}