module.exports = {
    install: function({ path, fileName }, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "apkInstaller", "install", [path, fileName]);
    },
};
