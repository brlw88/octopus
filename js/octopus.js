/*const checkoutSprintXX = function(branch) {
    return new Promise(function(resolve, reject) {
        shell.execAsync('git checkout sprint-' + branch,
            (errorlevel) => errorlevel === 0 ? resolve() : reject(errorlevel));
    });
};*/

/*
acquireOctopus()
    .then(fetchWithTags)
    .then(()=>checkoutSprintXX(27))
    .then(pullFromOrigin)
    .then(checkoutCupisXXXX(4299))
    .then(()=>rebaseToSprintXX(27))
    .then(()=>checkoutSprintXX(27))
    .then(()=>mergeFFWithCupisXXXX(4299))
    .then(pushToOrigin)
    .then(releaseOctopus)
    .catch(reason=>fuckItOff(reason));
*/

class Octopus {
    constructor () {};
}

const octopus = {
    octopusItself: new Octopus(),
    acquired: false
};

async function doMergeWorkflow(sprintNo, featureNo) {
    function acquireOctopus() {
        return new Promise(function (resolve, reject) {
            const ATTEMPTS = 5, RECHECK_TIME = 1000;
            (function tryAcquireOctopus(attempts) {
                if (octopus.acquired) {
                    console.log("Octopus already acquired by somebody else, waiting");
                    --attempts ? setTimeout(()=>tryAcquireOctopus(attempts), RECHECK_TIME) : reject("Fed up with waiting!");
                }
                else {
                    octopus.acquired = true;
                    resolve(octopus.octopusItself);
                }
            })(ATTEMPTS);
        });
    }

    function noop() {
        return new Promise(function (resolve) {
            setTimeout(()=>resolve(), 500)
        })
    }

    const fetchWithTags = noop;
    const checkoutSprintXX = noop;
    const pullFromOrigin = noop;
    const checkoutCupisXXXX = noop;
    const resolveConflicts = noop;

    function rebaseToSprintXX(sprintNo) {
        let hasConflicts = true;
        return new Promise(function (resolve) {
            setTimeout(()=>resolve(hasConflicts), 500)
        })
    }

    console.log("Starting merge workflow, trying to acquire octopus...")
    try {
        await acquireOctopus();
        console.log("Octopus successfully acquired");

        await fetchWithTags();
        console.log("Fetched from origin successfully");

        await checkoutSprintXX(sprintNo);
        console.log("Checkout to SPRINT-" + sprintNo + " succeeded");

        await pullFromOrigin();
        console.log("Pull from origin succeeded");

        await checkoutCupisXXXX(featureNo);
        console.log("Checkout to CUPIS-" + featureNo + " succeeded");

        let hasConflicts;
        await rebaseToSprintXX(sprintNo).then((result)=>hasConflicts = result);
        if (hasConflicts) {
            console.log("Rebase of CUPIS-" + featureNo + " to SPRINT-" + sprintNo + " done with conflicts");
            await resolveConflicts(featureNo);
            console.log("Conflicts successfully resolved");
        }
        else
            console.log("Rebase of CUPIS-" + featureNo + " to SPRINT-" + sprintNo + " succeeded");

        await checkoutSprintXX(sprintNo);
        console.log("Checkout to SPRINT-" + sprintNo + " succeeded");

/*        await mergeFFWithCupisXXXX(featureNo);
        await pushToOrigin(featureNo);
        await releaseOctopus(); */
    } catch (e) {
        console.log("Error in merge workflow: " +  e);
        return;
    }
    console.log("Merge workflow completed successfully");
}

octopus.acquired = true;
setTimeout(() => octopus.acquired = false, 2000);

doMergeWorkflow(27, 4299);
