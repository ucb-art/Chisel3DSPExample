Chisel3 DSP Template
===================

This repo requires that you've setup + updated [Chisel3DSPDependencies](https://github.com/ucb-art/Chisel3DSPDependencies/blob/master/README.md) somewhere. Don't forget to go into the **Chisel3DSPDependencies** directory and `source setenv.bash` each time you start a new session (unless you're doing `source update.bash` for the first time! Then you can run `sbt test` in this directory to run tests on example code. 

> Note: If you want to run a particular set of tests (i.e. whatever tests in `class SimpleTBSpec extends FlatSpec with Matchers`), you can use something like `sbt "test-only SimpleTB.SimpleTBSpec"` (consisting of packageName.testName). 

You can use this repo as a starting point for your own DSP blocks by cloning and then re-initializing the repo via:

```
git clone git@github.com:ucb-art/Chisel3DSPExample.git YourDSPBlock
cd YourDSPBlock
rm -rf .git
git init
git commit -m "Initial commit of YourDSPBlock."
```

Change the name of your block in **build.sbt**.

Don't forget to update the readme, add a new remote, & push (and delete the Examples directories in *src/test/scala*)! Note that *src/test/scala/Examples* will be updated with snippets of code that you'll probably end up using to make your DSP blocks. 

Examples
===================

Inside the **Examples** directory, you'll find **SimpleDspModuleSpec.scala**. This shows you how to create a basic module (that adds two numbers) and test it. To better understand how to use DSP features (type classes, testers, etc.) check out the [README](https://github.com/ucb-bar/dsptools/blob/add_ops/src/main/scala/dsptools/numbers/).README.md)!

Note that if you choose to generate a corresponding Verilog TB form your peek/pokes, you might try simulating things online at [EDA Playground](https://www.edaplayground.com).
