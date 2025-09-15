<script lang="ts">
  import { onMount } from 'svelte';
  import { API_BASE } from '../config.js';

  export let currentUser = null; // hent fra App.svelte

  let polls = [];
  let selectedPollId: number = null;
  let selectedPoll = null;
  let selectedOption = '';
  let loading = true;

  $: selectedPoll = polls.find(p => p.id === selectedPollId);

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
        }
      }
    } catch (err) {
      console.error("Error fetching polls", err);
    } finally {
      loading = false;
    }
  });

  async function submitVote(option) {
    if (!currentUser) {
      alert('You must be logged in to vote!');
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

      if (!res.ok) {
        console.error('Vote not accepted', await res.text());
        return;
      }

      const newVote = await res.json();
      selectedPoll.votes = [...selectedPoll.votes, newVote];
      selectedOption = option.caption;

    } catch (err) {
      console.error('Network error while voting', err);
    }
  }

  function countVotes(optionId: number) {
    return selectedPoll?.votes.filter(v => v.voteOptionId === optionId).length || 0;
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
    <select bind:value={selectedPollId}>
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
              {option.caption} — {countVotes(option.id)} votes ({votePercentage(option.id)}%)
            </button>
          {/if}
        {/each}
      </div>
    {/if}

    {#if selectedOption}
      <p style="margin-top: 2rem; color: #ffcc00;">
        You voted for: <strong>{selectedOption}</strong>
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