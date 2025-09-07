package no.hvl.dat250.assignment2.model;

public class VoteOption {
    private Long id;
    private Long pollId;
    private String caption;
    private int presentationOrder;

    public VoteOption() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getPollId() { return pollId; }
    public void setPollId(Long pollId) { this.pollId = pollId; }

    public String getCaption() {return caption;}
    public void setCaption(String caption) {this.caption = caption;}

    public int getPresentationOrder() {return presentationOrder;}
    public void setPresentationOrder(int presentationOrder) {this.presentationOrder = presentationOrder;}
}
