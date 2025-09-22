package no.hvl.dat250.jpa.polls;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import no.hvl.dat250.assignment2.model.Poll;
import no.hvl.dat250.assignment2.model.User;
import no.hvl.dat250.assignment2.model.Vote;
import no.hvl.dat250.assignment2.model.VoteOption;


public class PollsTest {

    private EntityManagerFactory emf;


    private void populate(EntityManager em) {
        User alice = new User("alice", "alice@online.com");
        User bob = new User("bob", "bob@bob.home");
        User eve = new User("eve", "eve@mail.org");
        em.persist(alice);
        em.persist(bob);
        em.persist(eve);
        Poll poll = alice.createPoll("Vim or Emacs?");
        VoteOption vim = poll.addVoteOption("Vim");
        VoteOption emacs = poll.addVoteOption("Emacs");
        Poll poll2 = eve.createPoll("Pineapple on Pizza");
        VoteOption yes = poll2.addVoteOption("Yes! Yammy!");
        VoteOption no = poll2.addVoteOption("Mamma mia: Nooooo!");
        em.persist(poll);
        em.persist(poll2);
        em.persist(alice.voteFor(vim));
        em.persist(bob.voteFor(vim));
        em.persist(eve.voteFor(emacs));
        em.persist(eve.voteFor(yes));
    }

    @BeforeEach
    public void checkPersistenceXml() {
        // Sjekk om META-INF/persistence.xml er på classpath
        var url = PollsTest.class.getClassLoader().getResource("META-INF/persistence.xml");
        if (url == null) {
            System.out.println("💥 Kan IKKE finne persistence.xml på classpath!");
        } else {
            System.out.println("✅ Fant persistence.xml: " + url);
        }
    }

    @BeforeEach
    public void setUp() {
        EntityManagerFactory emf = new PersistenceConfiguration("polls")
                .managedClass(Poll.class)
                .managedClass(User.class)
                .managedClass(Vote.class)
                .managedClass(VoteOption.class)
                .property(PersistenceConfiguration.JDBC_URL, "jdbc:h2:mem:polls")
                .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, "drop-and-create")
                .property(PersistenceConfiguration.JDBC_USER, "sa")
                .property(PersistenceConfiguration.JDBC_PASSWORD, "")
                .createEntityManagerFactory();
        emf.runInTransaction(em -> {
            populate(em);
        });
        this.emf = emf;
    }

    @Test
    public void testUsers() {
        emf.runInTransaction(em -> {
            Integer actual = (Integer) em.createNativeQuery("select count(id) from users", Integer.class).getSingleResult();
            assertEquals(3, actual);

            User maybeBob = em.createQuery("select u from User u where u.username like 'bob'", User.class).getSingleResultOrNull();
            assertNotNull(maybeBob);
        });
    }

    @Test
    public void testVotes() {
        emf.runInTransaction(em -> {
            Long vimVotes = em.createQuery("select count(v) from Vote v join v.votesOn as o join o.poll as p join p.createdBy u where u.email = :mail and o.presentationOrder = :order", Long.class).setParameter("mail", "alice@online.com").setParameter("order", 0).getSingleResult();
            Long emacsVotes = em.createQuery("select count(v) from Vote v join v.votesOn as o join o.poll as p join p.createdBy u where u.email = :mail and o.presentationOrder = :order", Long.class).setParameter("mail", "alice@online.com").setParameter("order", 1).getSingleResult();
            assertEquals(2, vimVotes);
            assertEquals(1, emacsVotes);
        });
    }

    @Test
    public void testOptions() {
        emf.runInTransaction(em -> {
            List<String> poll2Options = em.createQuery("select o.caption from Poll p join p.options o join p.createdBy u where u.email = :mail order by o.presentationOrder", String.class).setParameter("mail", "eve@mail.org").getResultList();
            List<String> expected = Arrays.asList("Yes! Yammy!", "Mamma mia: Nooooo!");
            assertEquals(expected, poll2Options);
        });
    }
}
