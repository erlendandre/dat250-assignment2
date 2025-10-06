package no.hvl.dat250.assignment2.dto;

public class VoteCount {
    private String optionCaption;
    private int voteCount;

    public VoteCount() {}

    public VoteCount(String optionCaption, int voteCount) {
        this.optionCaption = optionCaption;
        this.voteCount = voteCount;
    }

    public String getOptionCaption() {return optionCaption;}
    public void setOptionCaption(String optionCaption) {this.optionCaption = optionCaption;}

    public int getVoteCount() {return voteCount;}
    public void setVoteCount(int voteCount) {this.voteCount = voteCount;}
}