<script lang="ts">
  import { onMount } from 'svelte';
  import { API_BASE } from '../config.js';

  export let currentUser = null;

  let polls = [];
  let selectedPollId: number = null;
  let selectedPoll = null;
  let selectedOption = '';
  let voteMessage = '';
  let loading = true;


  onMount(async () => {
    try {
      const res = await fetch(`${API_BASE}/polls`);
      if (res.ok) {
        let data = await res.json();
        let userId = currentUser ? Number(currentUser.id) : null;

        polls = data.filter(poll => {
          if (poll.public) return true;
          if (userId != null && poll.invitedUserIds.includes(userId)) return true;
          return false;
        });

        if (polls.length > 0) {
          selectedPollId = polls[0].id;
          await fetchSelectedPoll(selectedPollId);
        }
      }
    } catch (err) {
      console.error("Error fetching polls", err);
    } finally {
      loading = false;
    }
  });

  async function fetchSelectedPoll(pollId: number) {
    try {
      const res = await fetch(`${API_BASE}/polls/${pollId}`);
      if (!res.ok) throw new Error(`Failed to fetch poll with id ${pollId}`);

      selectedPoll = await res.json();
    } catch (err) {
      console.error("Failed to fetch selected poll", err);
    }
  }

  async function submitVote(option) {
    if (!currentUser) {
        alert('You must be logged in to vote!');
        return;
    }

    const existingVote = selectedPoll.votes.find(v => v.user.id === currentUser.id);
    if (existingVote && existingVote.votesOn.id === option.id) {
        voteMessage = `You already voted for this option`;
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/polls/${selectedPoll.id}/votes`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                userId: currentUser.id,
                voteOptionId: option.id
            })
        });

        const data = await res.json();

        if (!res.ok) {
            voteMessage = `Vote not accepted: ${data.error || data.message}`;
            return;
        }

        const existingIndex = selectedPoll.votes.findIndex(v => v.user.id === currentUser.id);
        if (existingIndex >= 0) {
            selectedPoll.votes[existingIndex] = data;
            voteMessage = `You changed your vote to: ${option.caption}`;
        } else {
            selectedPoll.votes = [...selectedPoll.votes, data];
            voteMessage = `You voted for: ${option.caption}`;
        }

        selectedOption = option.caption;

    } catch (err) {
      console.error('Network error while voting', err);
      voteMessage = 'Network error while voting';
    }
  }

  function countVotes(optionId: number) {
    return selectedPoll?.votes.filter(v => v.votesOn.id === optionId).length || 0;
  }


  function votePercentage(optionId: number) {
    const total = selectedPoll?.votes.length || 0;
    if (total === 0) return 0;
    return Math.round((countVotes(optionId) / total) * 100);
  }

</script>

<div class="card">
  <h3>Available polls:</h3>

  {#if loading}
    <p>Loading polls...</p>
  {:else if polls.length === 0}
    <p>No polls available for you.</p>
  {:else}
    <select bind:value={selectedPollId} on:change={async () => {
      if (selectedPollId) {
        await fetchSelectedPoll(selectedPollId);
        selectedOption = '';
        voteMessage = '';
      }
    }}>
      {#each polls as poll (poll.id)}
        <option value={poll.id}>{poll.question}</option>
      {/each}
    </select>
    {#if selectedPoll && selectedPoll.options.length > 0}
      <h3 style="margin-top: 1.5rem;">Options:</h3>
      <div class="options">
        {#each selectedPoll.options as option (option.id)}
          {#if option.caption}
            <button on:click={() => submitVote(option)}>
              {option.caption} - {countVotes(option.id)} votes ({votePercentage(option.id)}%)
            </button>
          {/if}
        {/each}
      </div>
    {/if}

    {#if voteMessage}
      <p style="margin-top: 2rem; color: #ffcc00;">
        {voteMessage}
      </p>
    {/if}
  {/if}
</div>

<style>
  .options {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin-top: 1rem;
  }

  .options button {
    width: 100%;
    text-align: left;
    padding: 0.6rem 1rem;
    border-radius: 6px;
    background-color: #1a1a1a;
    border: 1px solid transparent;
    color: #fff;
    cursor: pointer;
    transition: border-color 0.25s, background-color 0.25s;
  }

  .options button:hover {
    border-color: #646cff;
    background-color: #1a1aff20;
  }
</style>