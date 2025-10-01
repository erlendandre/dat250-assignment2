package no.hvl.dat250.assignment2.model;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"created"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, message="Username must be at least 3 characters")
    private String username;

    @NotBlank
    @Email(message = "Email must be valid")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, message="Password must be at least 6 characters")
    private String password;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Poll> created = new LinkedHashSet<>();

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.created = new LinkedHashSet<>();
    }

    public User(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public Poll createPoll(String question) {
        Poll poll = new Poll();
        poll.setQuestion(question);
        poll.setCreatedByUser(this);
        this.created.add(poll);
        return poll;
    }

    public Vote voteFor(VoteOption option) {
        if (option == null) {
            throw new IllegalArgumentException("VoteOption cannot be null");
        }

        Vote vote = new Vote();
        vote.setUser(this);
        vote.setPoll(option.getPoll());
        vote.setVotesOn(option);
        vote.setPublishedAt(java.time.Instant.now());
        vote.setLastUpdatedAt(java.time.Instant.now());

        option.getVotes().add(vote);
        option.getPoll().getVotes().add(vote);

        return vote;
    }
}
