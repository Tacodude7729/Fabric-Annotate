package sh.emberj.annotate.test;

import sh.emberj.annotate.registry.Register;

@Register(registry = AnimalRegistry.ID, path = "piglet")
@Register(registry = AnimalRegistry.ID)
public class Pig extends Animal {

    @Override
    public void makeNoise() {
        System.out.println("Oink!");
    }

}
