<script>
  import { onMount } from 'svelte';
  export let onDone;
  export let currentUser;

  let pollQuestion = '';
  let options = ['', ''];
  let newOption = '';
  let message = '';
  let isPublic = true;
  let allUsers = [];
  let invitedUserIds = new Set();

  onMount(async () => {
    const res = await fetch('http://localhost:8080/users');
    allUsers = await res.json();
  });

  function toggleInvite(userId) {
    if (invitedUserIds.has(userId)) invitedUserIds.delete(userId);
    else invitedUserIds.add(userId);
  }

  function addOption() {
    if (newOption.trim() !== '') {
      options = [...options, newOption];
      newOption = '';
    }
  }

  function removeOption(index) {
    options = options.filter((_, i) => i !== index);
  }

  async function createPoll() {
    const poll = {
      question: pollQuestion,
      options: options.filter(o => o.trim() !== '').map(text => ({ text })),
      isPublic,
      invitedUserIds: Array.from(invitedUserIds),
      username: currentUser.username
    };

    console.log(JSON.stringify(poll));

    try {
      const res = await fetch('http://localhost:8080/polls', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(poll)
      });

      if (res.ok) {
        message = 'Poll created successfully!';
        pollQuestion = '';
        options = ['', ''];
        invitedUserIds.clear();
        if (onDone) onDone();
      } else {
        message = 'Error creating poll';
      }
    } catch (err) {
      console.error(err);
      message = 'Network error';
    }
  }
</script>

<section class="card">
  <h3>
    Poll title:
    <input type="text" bind:value={pollQuestion} placeholder="Enter poll title" />
  </h3>

  <h3>Options:</h3>
  {#each options as option, index}
    <nav style="display:flex; align-items:center; margin-bottom:0.5rem;">
      <input type="text" bind:value={options[index]} placeholder={`Option ${index + 1}`} />
      <button on:click={() => removeOption(index)} style="margin-left:0.5rem;">Remove</button>
    </nav>
  {/each}

  <nav style="margin-bottom:1rem;">
    <input type="text" bind:value={newOption} placeholder="Add new option" />
    <button on:click={addOption} style="margin-left:0.5rem;">Add option</button>
  </nav>

  <label style="display: inline-flex; align-items: center; gap: 0.5rem;">
    Public poll?
    <input type="checkbox" bind:checked={isPublic} style="outline:none; width: auto; margin: 0;">
  </label>

  {#if !isPublic && currentUser}
    <h4>Invite users to vote:</h4>
    {#each allUsers as user (user.id)}
      {#if user.id !== currentUser.id}
        <label style="display:flex; align-items:center; gap:0.5rem; margin-bottom:0.3rem;">
          {user.username}
          <input type="checkbox" style="outline:none;"
                checked={invitedUserIds.has(user.id)}
                on:change={() => toggleInvite(user.id)} />
        </label>
      {/if}
    {/each}
  {/if}

  <nav>
    <button on:click={createPoll} style="margin-top:1rem;">Create poll</button>
  </nav>

  {#if message}
    <p style="margin-top: 1rem; color: #ffcc00;">{message}</p>
  {/if}
</section>