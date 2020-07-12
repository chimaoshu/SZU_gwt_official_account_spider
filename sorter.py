def showList(lines):
    number = 0
    print('======================================================')
    for line in list(lines):
        if line[0] == '1':
            number += 1
            information = line.split('$')
            title = information[1]
            department = information[2]
            print(number,'  '+title+'   '+department)
            #date = information[3]
            #link = information[4]
    else:
        print('======================================================')

if __name__ == "__main__":
    
    import sys
    import os

    #读取本地公文信息
    with open('todayList.txt','r+',encoding='UTF-8') as f:
        lines = f.readlines()
        showList(lines)

        while True:
            try:
                userInput = input('输入要提到最前的公文序号\n输入1,4把第一则调换到第四则的位置\n输入quit完成操作\n')
                if userInput == 'show':
                    pass
                elif userInput == 'quit':
                    break
                elif ',' in userInput or '，' in userInput:
                    x = userInput.split(',') 
                    strat_index = int(x[0])-1
                    end_index = int(x[1])-1
                    temp = lines[strat_index]
                    lines.pop(strat_index)
                    lines.insert(end_index,temp)
                    
                else:
                    try:
                        index = int(userInput)-1
                        temp = lines[index]
                        lines.pop(index)
                        lines.insert(0,temp)
                        
                    except() as e:
                        print(e)
                os.system('cls')
                showList(lines)
            except:
                pass

        f.seek(0,0)
        f.truncate()
        for line in list(lines):
            f.write(line)
