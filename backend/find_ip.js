const os = require('os');
const networkInterfaces = os.networkInterfaces();

console.log("\n--- YOUR LOCAL IP ADDRESSES ---");
Object.keys(networkInterfaces).forEach((interfaceName) => {
    networkInterfaces[interfaceName].forEach((details) => {
        if (details.family === 'IPv4' && !details.internal) {
            console.log(`Interface [${interfaceName}]: ${details.address}`);
        }
    });
});
console.log("-------------------------------\n");
console.log("Use one of the addresses above (likely starting with 192.168.x.x) in your RetrofitClient.kt file.");
