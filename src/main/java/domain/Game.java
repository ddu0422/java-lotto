package domain;

import java.util.*;

class Game {

    private int inputPrice;
    private int purchaseCount;
    private Lotto[] lottos;
    private List<Integer> winningLottoNumber;
    private int bonusLottoNumber;

    void run() {
        userInputPrice();
        createLotto();
        repeatOutputLottoNumbers();
        inputWinningLotto();
        outputResult();
    }

    private void userInputPrice() {
        System.out.println("구입금액을 입력해주세요.");
        do {
            buyLotto();
            System.out.println();
        } while (printPurchaseCount());
    }

    private void createLotto() {
        createLottoObjectArray();
        createLottoInstance();
    }

    private void inputWinningLotto() {
        inputLastWeekWinningLotto();
        inputBonusBallLotto();
    }

    private void outputResult() {
        Map<Rank, Integer> rankLinkedHashMap = matchLottoNumber();
        outputWinMessage();
        outputWinStats(rankLinkedHashMap);
        outputRevenueCalculation(revenueCalculation(rankLinkedHashMap, inputPrice));
    }

    private void buyLotto() {
        Scanner sc = new Scanner(System.in);

        try {
            inputPrice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("올바른 숫자가 아닙니다.");
            buyLotto();
        }
    }

    private boolean printPurchaseCount() {
        purchaseCount = inputPrice / 1000;

        if (purchaseCount <= 0) {
            System.out.println("로또를 살 수 없습니다.");
            return true;
        }

        System.out.println(purchaseCount + "개를 구매했습니다.");
        return false;
    }

    private void createLottoObjectArray() {
        lottos = new Lotto[purchaseCount];
    }

    private boolean checkSizeNumbers(List<Integer> numbers) {
        return !(numbers.size() == 6);
    }

    private int createRandomNumber() {
        return (int) (Math.random() * 45) + 1;
    }

    private void addNumbers(List<Integer> numbers, int randomNumber) {
        if (numbers.indexOf(randomNumber) == -1) {
            numbers.add(randomNumber);
        }
    }

    private void sortNumbers(List<Integer> numbers) {
        Collections.sort(numbers);
    }

    private List<Integer> createLottoNumbers() {
        List<Integer> numbers = new ArrayList<>();

        while (checkSizeNumbers(numbers)) {
            int randomNum = createRandomNumber();
            addNumbers(numbers, randomNum);
            sortNumbers(numbers);
        }

        return numbers;
    }

    private void createLottoInstance() {
        for (int i = 0; i < purchaseCount; i++) {
            lottos[i] = new Lotto(createLottoNumbers());
        }
    }

    private void outputLottoNumbers(Lotto lotto) {
        String numbers = lotto.getNumbers().toString();

        System.out.println(numbers);
    }

    private void repeatOutputLottoNumbers() {
        for (Lotto lotto : lottos) {
            outputLottoNumbers(lotto);
        }
        System.out.println();
    }

    private List<Integer> splitWithComma(String inputLottoNumber) {
        String[] inputArray = inputLottoNumber.split(",");
        List<String> inputLotto = Arrays.asList(inputArray);
        List<Integer> lottoNumbers = new ArrayList<>();

        try {
            for (String number : inputLotto) {
                lottoNumbers.add(Integer.parseInt(number));
            }
        } catch (NumberFormatException e) {
            System.out.println("올바른 숫자가 아닙니다.");
        }

        return lottoNumbers;
    }

    private String removeBlank(String inputLottoNumber) {
        return inputLottoNumber.replaceAll(" ", "");
    }

    private List<Integer> inputLottoNumber() {
        Scanner sc = new Scanner(System.in);

        String inputLottoNumber = removeBlank(sc.nextLine());

        return splitWithComma(inputLottoNumber);
    }

    private boolean checkNumberRangeAndFormat(int lottoNumber) {
        try {
            if (lottoNumber < 1 || lottoNumber > 45) {
                System.out.println("숫자가 너무 큽니다. 1 ~ 45 사이로 입력해주세요.");
                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("숫자가 아닙니다. 다시 입력해주세요.");
            return true;
        }

        return false;
    }

    private boolean checkLottoNumberRangeAndFormat(List<Integer> lottoNumbers) {
        List<Boolean> checkNumber = new ArrayList<>();

        for (int lottoNumber : lottoNumbers) {
            checkNumber.add(checkNumberRangeAndFormat(lottoNumber));
        }

        return checkNumber.contains(true);
    }

    private boolean checkNumberCount(List<Integer> numbers, int listSize) {
        if (numbers.size() != listSize) {
            System.out.println("숫자 개수를 " + listSize + "로 맞추세요.");
            return true;
        }

        return false;
    }

    private List<Integer> inputLottoSelectSize(int listSize) {
        List<Integer> lottoNumbers;

        do {
            lottoNumbers = inputLottoNumber();
        } while (checkNumberCount(lottoNumbers, listSize)
                || checkLottoNumberRangeAndFormat(lottoNumbers));

        return lottoNumbers;
    }

    private void inputLastWeekWinningLotto() {
        System.out.println("지난 주 당첨 번호를 입력해 주세요.");
        winningLottoNumber = inputLottoSelectSize(6);
    }

    private void inputBonusBall() {
        System.out.println("보너스 볼을 입력해 주세요.");
        bonusLottoNumber = inputLottoSelectSize(1).get(0);
    }

    private boolean checkBonusBall() {
        if (winningLottoNumber.contains(bonusLottoNumber)) {
            System.out.println("보너스 번호는 지난 주 당첨 번호와 달라야 합니다.");
            return true;
        }

        return false;
    }

    private void inputBonusBallLotto() {
        do {
            inputBonusBall();
        } while (checkBonusBall());
    }

    private WinningLotto createWinningLotto() {
        Lotto lotto = new Lotto(winningLottoNumber);

        return new WinningLotto(lotto, bonusLottoNumber);
    }

    private Map<Rank, Integer> initMap() {
        Map<Rank, Integer> rankLinkedHashMap = new LinkedHashMap<>();
        Rank[] ranks = Rank.values();

        for (int i = 5; i >= 0; i--) {
            rankLinkedHashMap.put(ranks[i], 0);
        }

        return rankLinkedHashMap;
    }

    private Map<Rank, Integer> matchLottoNumber() {
        Map<Rank, Integer> rankLinkedHashMap = initMap();

        for (Lotto lotto : lottos) {
            Rank rank = createWinningLotto().match(lotto);
            rankLinkedHashMap.put(rank, rankLinkedHashMap.get(rank) + 1);
        }

        return rankLinkedHashMap;
    }

    private void outputWinMessage() {
        System.out.println();
        System.out.println("당첨 통계");
        System.out.println("--------");
    }

    private void outputRankMessage(Rank rank, int count) {
        if (rank == Rank.MISS) {
            return;
        }
        System.out.print(rank.getCountOfMatch() + "개 일치");
        if (rank == Rank.SECOND) {
            System.out.print(", 보너스 볼 일치");
        }
        System.out.print("(" + rank.getWinningMoney() + "원)- ");
        System.out.println(count + "개");
    }

    private void outputWinStats(Map<Rank, Integer> rankLinkedHashMap) {
        for (Map.Entry<Rank, Integer> rankEntry : rankLinkedHashMap.entrySet()) {
            outputRankMessage(rankEntry.getKey(), rankEntry.getValue());
        }
    }

    private double revenueCalculation(Map<Rank, Integer> rankLinkedHashMap, int inputPrice) {
        double totalPrice = 0;

        for (Map.Entry<Rank, Integer> rankEntry : rankLinkedHashMap.entrySet()) {
            totalPrice += rankEntry.getKey().getWinningMoney() * rankEntry.getValue();
        }

        return totalPrice / inputPrice;
    }

    private void outputRevenueCalculation(double revenuePrice) {
        System.out.println("총 수익률은 " + revenuePrice + "입니다.");
    }
}
