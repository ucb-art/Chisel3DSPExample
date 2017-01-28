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

Don't forget to update the readme, add a new remote, & push (and delete the *Examples directories in *src/test/scala*)! Note that *src/test/scala/UsefulExamples* will be updated with snippets of code that you'll probably end up using to make your DSP blocks. 

Examples
===================

Inside the **UsefulExamples** directory, you'll find **SimpleTBwGenTypeOption.scala**. This shows you various ways that you can make your test bench (implemented as parallely run Scala tests) interact with a module's IO and internal signals (including Literals)! 

* By extending the standard VerboseDspTester with the trait EasyPeekPoke, it generalizes poke/dspPoke & expect/dspExpect as feed and check respectively (so you don't have to think about which to use). It's important to know that Scala Doubles are always rounded when peeking/poking SInts and UInts. In the latter case, the absolute value is also taken. 
* It demonstrates how to update expect tolerances for fixed point checks via *fixTolLSBs.withValue*. 
* More generally, it shows how you can parameterize your module generators and use type classes that enable better systems modeling (i.e. allows you to easily switch between running your circuit with both FixedPoint/SInt numbers and DspReals).
* It shows you how to generate constants of your generic types (i.e. via `genShort.double2T(#)` or `genShort.double2TFixedWidth(#)`).
* It demonstrates how to optionally include IO with Scala Option.
* It shows you how to create test bench specs with test bench options passed in through an options manager (in the object TestSetup). This allows you to control tester Verbosity (number *and* bit level), change your display base (in the case of bit level), and change your expect tolerance (fixTolLSBs for FixedPoint and realTolDecPts for DspReal). You can also change the simulation parameters. Please refer to the code [here](https://github.com/ucb-bar/dsptools/blob/master/src/main/scala/dsptools/VerboseDspTesterOptions.scala) for more details!
* Most importantly, when *genVerilogTb* is set to true, a corresponding Verilog TB will be dumped into the run directory that can then be used, along with the synthesizable Verilog, to check hardware functionality. You might even try simulating things online at [EDA Playground](https://www.edaplayground.com).
* Also proves that the decimal point gets propagated correctly when assigning between nodes with different fractional bits