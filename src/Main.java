import java.util.Random;

public class Main {
    public static int bossHealth = 700;
    public static int bossDamage = 50;
    public static String bossDefence;
    public static String[] heroesAttackType = {"Physical", "Magical", "Kinetic", "Medic", "Golem", "Lucky", "Berserk", "Thor"};
    public static int[] heroesHealth = {270, 260, 250, 240};
    public static int[] heroesDamage = {10, 15, 20, 0};
    public static int roundNumber = 0;

    public static void main(String[] args) {
        printStatistics();
        while (!isGameOver()) {
            playRound();
        }
    }

    public static void playRound() {
        roundNumber++;
        chooseBossDefence();
        bossHits();
        heroesHit();
        printStatistics();
    }
    public static void chooseBossDefence() {
        Random random = new Random();
        int randomIndex = random.nextInt(heroesAttackType.length); //0,1,2
        bossDefence = heroesAttackType[randomIndex];
    }

    public static void heroesHit() {
        boolean firstHero = false;
        for (int i = 0; i < heroesDamage.length; i++) {
            if (bossHealth > 0 && heroesHealth[i] > 0) {
                int damage = heroesDamage[i];

                if(heroesAttackType[i] == "Medic") {
                    for (int j = 0; j < heroesHealth.length; j++) {
                        if (heroesHealth[j] < 100 && !firstHero && heroesHealth[j] > 0 && heroesAttackType[j] != "Medic") {
                            heroesHealth[j] = heroesHealth[j] + 5;
                            firstHero = true;
                        }
                    }
                }
                if (heroesAttackType[i] == bossDefence) {
                    Random random = new Random();
                    int coeff = random.nextInt(9) + 2; // 2,3,4,5,6,7,8,9,10
                    damage = heroesDamage[i] * coeff;
                    System.out.println("Critical damage: " + damage);
                }
                if (bossHealth - damage < 0) {
                    bossHealth = 0;
                } else {
                    bossHealth = bossHealth - damage;
                }
            }
        }
    }
    public static void bossHits() {
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                if (heroesHealth[i] - bossDamage < 0) {
                    heroesHealth[i] = 0;
                } else {
                    heroesHealth[i] = heroesHealth[i] - bossDamage;
                }
            }
        }
    }

    public static boolean isGameOver() {
        if (bossHealth <= 0) {
            System.out.println("Heroes won!!!");
            return true;
        }
        /*if (heroesHealth[0] <= 0 && heroesHealth[1] <= 0 && heroesHealth[2] <= 0) {
            System.out.println("Boss won!!!");
            return true;
        }
        return false;*/
        boolean allHeroesDead = true;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                allHeroesDead = false;
                break;
            }
        }
        if (allHeroesDead) {
            System.out.println("Boss won!!!");
        }
        return allHeroesDead;
    }

    public static void printStatistics() {
        /*String defence;
        if (bossDefence == null) {
            defence = "No Defence";
        } else {
            defence = bossDefence;
        }*/
        System.out.println("ROUND " + roundNumber + " --------------------");
        System.out.println("Boss health: " + bossHealth + " damage: " + bossDamage
                + " defence: " + (bossDefence == null ? "No Defence" : bossDefence));
        for (int i = 0; i < heroesHealth.length; i++) {
            System.out.println(heroesAttackType[i] + " health: " + heroesHealth[i] + " damage: " + heroesDamage[i]);
        }

    }
}

class Player {
    protected String name;
    protected int health;
    protected int damage;
    protected Random random;

    public Player(String name, int health, int damage) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.random = new Random();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println(name + " получил " + damage + " урона.");
    }

    public int dealDamage() {
        return damage;
    }
}

class Golem extends Player {
    public Golem(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public void takeDamage(int damage) {
        int reducedDamage = damage / 5;
        super.takeDamage(reducedDamage);
    }
}

class Lucky extends Player {
    private static final double DODGE_CHANCE = 0.2;

    public Lucky(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public void takeDamage(int damage) {
        if (random.nextDouble() <= DODGE_CHANCE) {
            System.out.println(name + " уклонился от удара.");
        } else {
            super.takeDamage(damage);
        }
    }
}

class Berserk extends Player {
    private static final double BLOCK_PERCENTAGE = 0.3;

    public Berserk(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public void takeDamage(int damage) {
        int blockedDamage = (int) (damage * BLOCK_PERCENTAGE);
        super.takeDamage(damage - blockedDamage);
        System.out.println(name + " заблокировал " + blockedDamage + " урона и вернул его боссу.");
    }

    @Override
    public int dealDamage() {
        int blockedDamage = (int) (damage * BLOCK_PERCENTAGE);
        int increasedDamage = blockedDamage;
        System.out.println(name + " блокирует " + blockedDamage + " урона и добавляет его к своему урону.");
        return super.dealDamage() + increasedDamage;
    }
}

class Thor extends Player {
    private static final double STUN_CHANCE = 0.3;

    public Thor(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public int dealDamage() {
        if (random.nextDouble() <= STUN_CHANCE) {
            System.out.println(name + " оглушил босса на 1 раунд.");
            return 0;
        } else {
            return super.dealDamage();
        }
    }
    public static void main(String[] args) {
        Player boss = new Player("Босс", 1000, 50);
        Player golem = new Golem("Голем", 500, 20);
        Player lucky = new Lucky("Счастливчик", 300, 30);
        Player berserk = new Berserk("Берсерк", 400, 40);
        Player thor = new Thor("Тор", 350, 35);

        Player[] players = {boss, golem, lucky, berserk, thor};

        // Пример битвы
        while (boss.getHealth() > 0) {
            Player attacker = players[new Random().nextInt(players.length)];
            Player target = players[new Random().nextInt(players.length)];
            if (attacker != target) {
                int damage = attacker.dealDamage();
                target.takeDamage(damage);
            }
        }
    }
}

