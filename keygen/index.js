const crypto = require('crypto');
const bip39 = require('bip39');
const BlindSignature = require('blind-signatures');

// Generate an RSA key pair for Bob
const Bob = {
  key: BlindSignature.keyGeneration({ b: 2048 }), // b: key-length
  blinded: null,
  unblinded: null,
  message: null,
};

function generateMnemonic() {
  // Generate a random seed (256 bits).
  const seedSize = 32; // 256 bits
  const seed = crypto.randomBytes(seedSize);

  // Generate a BIP39 mnemonic phrase from the seed.
  const mnemonic = bip39.entropyToMnemonic(seed.toString('hex'));

  // Set the text of the HTML element with ID "key_mnemonic" to the generated mnemonic.
  document.getElementById('key_mnemonic').textContent = mnemonic;

  // Alice wants Bob to sign a message without revealing its contents.
  const Alice = {
    message: seed.toString('hex'),
    N: null,
    E: null,
    r: null,
    signed: null,
    unblinded: null,
  };

  // Alice gets N and E variables from Bob's key
  Alice.N = Bob.key.keyPair.n.toString();
  Alice.E = Bob.key.keyPair.e.toString();

  // Alice blinds the message
  const { blinded, r } = BlindSignature.blind({
    message: Alice.message,
    N: Alice.N,
    E: Alice.E,
  });
  Alice.r = r;

  // Bob signs the blinded message
  const signed = BlindSignature.sign({
    blinded: blinded,
    key: Bob.key,
  });

  // Alice unblinds the signed message
  const unblinded = BlindSignature.unblind({
    signed: signed,
    N: Alice.N,
    r: Alice.r,
  });
  Alice.unblinded = unblinded;

  // Alice verifies the signature
  const result = BlindSignature.verify({
    unblinded: Alice.unblinded,
    N: Alice.N,
    E: Alice.E,
    message: Alice.message,
  });
  if (result) {
    console.log('Alice: Signatures verify!');
  } else {
    console.log('Alice: Invalid signature');
  }

  return { seed: '0x' + seed.toString('hex'), mnemonic: mnemonic };
}

window.generateMnemonic = generateMnemonic;
