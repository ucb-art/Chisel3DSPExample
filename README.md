Chisel3 DSP Template
===================

This repo requires that you've setup + updated [Chisel3DSPDependencies](https://github.com/ucb-art/Chisel3DSPDependencies/blob/master/README.md) somewhere. Then you can run `sbt test` in this directory to run tests on example code. 

You can use this repo as a starting point for your own DSP blocks by cloning and then re-initializing the repo via:

```
git clone git@github.com:ucb-art/Chisel3DSPExample.git YourDSPBlock
cd YourDSPBlock
rm -rf .git
git init
git commit -m "Initial commit of YourDSPBlock."
```

Change the name of your block in **build.sbt**.

Don't forget to update the readme, add a new remote, & push!