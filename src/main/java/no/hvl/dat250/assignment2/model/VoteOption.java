package no.hvl.dat250.assignment2.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "voteoptions")
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Poll poll;

    private String caption;
    private int presentationOrder;

    @OneToMany(mappedBy = "votesOn", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Vote> votes = new ArrayList<>();

    public VoteOption() {}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Poll getPoll() {return poll;}
    public void setPoll(Poll poll) {this.poll = poll;}

    public String getCaption() {return caption;}
    public void setCaption(String caption) {this.caption = caption;}

    public int getPresentationOrder() {return presentationOrder;}
    public void setPresentationOrder(int presentationOrder) {this.presentationOrder = presentationOrder;}

    public List<Vote> getVotes() {return votes;}
    public void setVotes(List<Vote> votes) {this.votes = votes;}
}
