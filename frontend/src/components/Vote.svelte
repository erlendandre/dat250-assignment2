<script>
  let polls = [
    { id: 1, title: 'Pineapple on pizza?', options: ['Yes', 'No', "Don't like pizza"] },
    { id: 2, title: 'Favorite programming language?', options: ['Java', 'Python', 'C++'] }
  ];
  let selectedPoll = polls[0];
  let selectedOption = '';
  
  function submitVote(option) {
    selectedOption = option;
    console.log('Voted for option:', selectedOption, 'in poll:', selectedPoll.title);
    // her kan vi bruke fetch() til å poste til backend
  }
</script>
  
<div class="card">
  
  <h3>
      Available polls:
    <select bind:value={selectedPoll}>
      {#each polls as poll}
        <option value={poll}>{poll.title}</option>
      {/each}
    </select>
  </h3>
  
  <h3>Options:</h3>
  <div class="options">
    {#each selectedPoll.options as option}
      <button on:click={() => submitVote(option)}>
        {option}
      </button>
    {/each}
  </div>
  
  {#if selectedOption}
  <p style="margin-top: 2rem; color: #ffcc00;">You voted for: <strong>{selectedOption}</strong></p>
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