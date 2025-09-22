package no.hvl.dat250.assignment2.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User createdBy;

    @NotBlank(message = "Poll question cannot be empty")
    private String caption;
    private Instant publishedAt;
    private Instant lastUpdatedAt;
    private Instant validUntil;
    private boolean isPublic;

    @ManyToMany
    @JoinTable(
        name = "poll_invited_users",
        joinColumns = @JoinColumn(name = "poll_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> invitedUsers = new HashSet<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
    
    @Size(min = 2, message = "Poll must have at least 2 options")
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options = new ArrayList<>();

    public Poll() {}

    public Poll(Long id, User createdBy, String caption, Instant publishedAt, Instant lastUpdatedAt, Instant validUntil, boolean isPublic) {
        this.id = id;
        this.createdBy = createdBy;
        this.caption = caption;
        this.publishedAt = publishedAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.validUntil = validUntil;
        this.isPublic = isPublic;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public User getCreatedByUser() {return createdBy;}
    public void setCreatedByUser(User createdBy) {this.createdBy = createdBy;}

    public String getQuestion() {return caption;}
    public void setQuestion(String caption) {this.caption = caption;}

    public Instant getPublishedAt() {return publishedAt;}
    public void setPublishedAt(Instant publishedAt) {this.publishedAt = publishedAt;}

    public Instant getLastUpdatedAt() {return lastUpdatedAt;}
    public void setLastUpdatedAt(Instant lastUpdatedAt) {this.lastUpdatedAt = lastUpdatedAt;}

    public Instant getValidUntil() {return validUntil;}
    public void setValidUntil(Instant validUntil) {this.validUntil = validUntil;}

    public boolean isPublic() {return isPublic;}
    public void setPublic(boolean isPublic) {this.isPublic = isPublic;}

    public Set<User> getInvitedUsers() {return invitedUsers;}
    public void setInvitedUsers(Set<User> invitedUsers) {this.invitedUsers = invitedUsers;}

    public List<Vote> getVotes() {return votes;}
    public void setVotes(List<Vote> votes) {this.votes = votes;}

    public List<VoteOption> getOptions() {return options;}
    public void setOptions(List<VoteOption> options) {this.options = options;}

    public VoteOption addVoteOption(String caption) {
        if (caption == null || caption.isBlank()) {
            throw new IllegalArgumentException("Caption cannot be null or blank");
        }

        VoteOption option = new VoteOption();
        option.setCaption(caption);
        option.setPoll(this);
        option.setPresentationOrder(options.size());

        options.add(option);
        return option;
    }
}
