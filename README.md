Small utility that will poll Stripe every minute and rang a desk bell every time a new payment is found.

## Build
Build the jar by running:

` ./gradlew jar`

## Usage

After building the jar, run:

`java -jar build/libs/stripe_bell-1.0-SNAPSHOT.jar <your_private_stripe_key>`
