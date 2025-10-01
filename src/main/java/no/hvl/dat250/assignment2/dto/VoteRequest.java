package no.hvl.dat250.assignment2.dto;

public class VoteRequest {
    private Long userId;
    private Long voteOptionId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getVoteOptionId() { return voteOptionId; }
    public void setVoteOptionId(Long voteOptionId) { this.voteOptionId = voteOptionId; }
}